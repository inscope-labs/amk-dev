package com.inscopelabs.amk.game.standardgo.controller

import com.inscopelabs.amk.game.standardgo.bot.MctsBot
import com.inscopelabs.amk.game.standardgo.bot.RandomBot
import com.inscopelabs.amk.game.standardgo.engine.Engine
import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.engine.PlayResult
import com.inscopelabs.amk.game.standardgo.io.GameSerializer
import com.inscopelabs.amk.game.standardgo.io.SgfParser
import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.GameState
import com.inscopelabs.amk.game.standardgo.model.PlayerType
import com.inscopelabs.amk.game.standardgo.model.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameController(
  initialConfig: GameConfig = GameConfig(),
  private val onPersistSnapshot: ((String) -> Unit)? = null,
) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private var engine = Engine(initialConfig)
  private var botJob: Job? = null

  private val _config = MutableStateFlow(initialConfig)
  val config: StateFlow<GameConfig> = _config.asStateFlow()

  private val _state = MutableStateFlow(engine.snapshot())
  val state: StateFlow<GameState> = _state.asStateFlow()

  private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 16)
  val events: SharedFlow<GameEvent> = _events.asSharedFlow()

  private val _isBotThinking = MutableStateFlow(false)
  val isBotThinking: StateFlow<Boolean> = _isBotThinking.asStateFlow()

  private val _pendingMove = MutableStateFlow<Pair<Int, Int>?>(null)
  val pendingMove: StateFlow<Pair<Int, Int>?> = _pendingMove.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  fun newGame(newConfig: GameConfig = _config.value) {
    botJob?.cancel()
    _isBotThinking.value = false
    _pendingMove.value = null
    _statusMessage.value = null
    _config.value = newConfig
    engine = Engine(newConfig)
    _state.value = engine.snapshot()
    persist()
    maybeTriggerBot()
  }

  fun onBoardTapped(row: Int, col: Int, requireConfirm: Boolean) {
    if (_isBotThinking.value || _state.value.isGameOver) return
    if (requireConfirm) {
      val currentPending = _pendingMove.value
      if (currentPending != null && currentPending.first == row && currentPending.second == col) {
        _pendingMove.value = null
        playAt(row, col)
      } else {
        _pendingMove.value = row to col
      }
    } else {
      playAt(row, col)
    }
  }

  fun confirmPendingMove() {
    val pending = _pendingMove.value ?: return
    _pendingMove.value = null
    playAt(pending.first, pending.second)
  }

  fun cancelPendingMove() {
    _pendingMove.value = null
  }

  fun playAt(row: Int, col: Int) {
    if (_isBotThinking.value) return
    applyMove(Move.place(row, col))
  }

  fun pass() {
    if (_isBotThinking.value) return
    _pendingMove.value = null
    applyMove(Move.pass())
  }

  fun resign() {
    botJob?.cancel()
    _isBotThinking.value = false
    _pendingMove.value = null
    applyMove(Move.resign())
  }

  fun undo() {
    if (_isBotThinking.value) return
    _pendingMove.value = null
    val stepsToUndo =
      if (_config.value.mode == GameMode.HUMAN_VS_BOT && _state.value.moveCount >= 2) 2 else 1
    var updated: GameState? = null
    repeat(stepsToUndo) {
      val prev = engine.undo()
      if (prev != null) updated = prev
    }
    updated?.let {
      _state.value = it
      _statusMessage.value = null
      persist()
    }
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun exportSgf(): String = SgfParser.export(engine.config, engine.moveLog)

  fun importSgf(sgf: String): Boolean {
    val parsed = SgfParser.import(sgf) ?: return false
    botJob?.cancel()
    _isBotThinking.value = false
    _pendingMove.value = null
    _config.value = parsed.config
    engine = Engine(parsed.config)
    for ((_, move) in parsed.moves) {
      engine.playMove(move)
    }
    _state.value = engine.snapshot()
    persist()
    return true
  }

  fun exportJson(): String = GameSerializer.serialize(_config.value, _state.value)

  fun importJson(json: String): Boolean {
    val restored = GameSerializer.deserialize(json) ?: return false
    botJob?.cancel()
    _isBotThinking.value = false
    _pendingMove.value = null
    _config.value = restored.first
    engine = Engine(restored.first)
    engine.restoreFromState(restored.second)
    _state.value = engine.snapshot()
    return true
  }

  private fun applyMove(move: Move) {
    val actingTeam = engine.currentTeam()
    when (val result = engine.playMove(move)) {
      is PlayResult.Rejected -> {
        _statusMessage.value = result.reason
        _events.tryEmit(GameEvent.IllegalMove(result.reason))
      }
      is PlayResult.Success -> {
        _statusMessage.value = null
        _state.value = result.state
        if (move.isPass) {
          _events.tryEmit(GameEvent.Pass(actingTeam))
        } else if (move.isPlacement) {
          _events.tryEmit(GameEvent.MoveMade(actingTeam, move))
          if (result.capturedCount > 0) {
            _events.tryEmit(GameEvent.Capture(actingTeam, result.capturedCount))
          }
        }
        if (result.state.isGameOver) {
          _events.tryEmit(
            GameEvent.GameOver(
              winner = result.state.winner,
              score = result.state.scoreSummary,
              resignedBy = result.state.resignedBy,
            )
          )
        } else {
          maybeTriggerBot()
        }
        persist()
      }
    }
  }

  private fun maybeTriggerBot() {
    val cfg = _config.value
    if (cfg.mode != GameMode.HUMAN_VS_BOT) return
    val current = _state.value
    if (current.isGameOver || current.currentTurn != Team.WHITE) return

    botJob?.cancel()
    _isBotThinking.value = true
    botJob =
      scope.launch {
        val snapshotEngine = engine.cloneForSimulation()
        val chosenMove =
          when (cfg.botType) {
            PlayerType.RANDOM_BOT -> RandomBot.getMove(snapshotEngine)
            else -> MctsBot.getMove(snapshotEngine, simulations = cfg.botSimulations)
          }
        _isBotThinking.value = false
        applyMove(chosenMove)
      }
  }

  private fun persist() {
    onPersistSnapshot?.invoke(exportJson())
  }
}
