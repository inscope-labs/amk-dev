package com.inscopelabs.amk.game.supergo.controller

import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.bot.TripleMctsBot
import com.inscopelabs.amk.game.supergo.engine.TripleEngine
import com.inscopelabs.amk.game.supergo.engine.TripleGameState
import com.inscopelabs.amk.game.supergo.engine.TriplePlayResult
import com.inscopelabs.amk.game.supergo.model.SuperGoConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripleGameController(
  initialConfig: SuperGoConfig = SuperGoConfig(),
) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private var engine = TripleEngine(initialConfig)
  private var botJob: Job? = null

  private val _config = MutableStateFlow(initialConfig)
  val config: StateFlow<SuperGoConfig> = _config.asStateFlow()

  private val _state = MutableStateFlow(engine.snapshot())
  val state: StateFlow<TripleGameState> = _state.asStateFlow()

  private val _isBotThinking = MutableStateFlow(false)
  val isBotThinking: StateFlow<Boolean> = _isBotThinking.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  private val _showSacrificeDialog = MutableStateFlow(false)
  val showSacrificeDialog: StateFlow<Boolean> = _showSacrificeDialog.asStateFlow()

  private val _showVetoDialog = MutableStateFlow(false)
  val showVetoDialog: StateFlow<Boolean> = _showVetoDialog.asStateFlow()

  fun newGame(newConfig: SuperGoConfig = _config.value) {
    botJob?.cancel()
    _isBotThinking.value = false
    _statusMessage.value = null
    _showSacrificeDialog.value = false
    _showVetoDialog.value = false
    _config.value = newConfig
    engine = TripleEngine(newConfig)
    _state.value = engine.snapshot()
  }

  fun selectBoard(boardIndex: Int) {
    engine.selectBoard(boardIndex)
    _state.value = engine.snapshot()
  }

  fun playAt(boardIndex: Int, row: Int, col: Int) {
    if (_isBotThinking.value) return
    applyMove(boardIndex, Move.place(row, col))
  }

  fun pass(boardIndex: Int = _state.value.activeBoardIndex) {
    if (_isBotThinking.value) return
    applyMove(boardIndex, Move.pass())
  }

  fun resign() {
    botJob?.cancel()
    _isBotThinking.value = false
    applyMove(_state.value.activeBoardIndex, Move.resign())
  }

  fun handOffRole() {
    if (_isBotThinking.value || _state.value.isGameOver) return
    _state.value = engine.handOffRole()
  }

  fun requestSacrificeDialog() {
    if (engine.canSacrifice()) {
      _showSacrificeDialog.value = true
    } else {
      _statusMessage.value = "Sacrifice requires ${_config.value.sacrificeCost} stones and no active cooldown."
    }
  }

  fun dismissSacrificeDialog() {
    _showSacrificeDialog.value = false
  }

  fun confirmSacrifice() {
    _showSacrificeDialog.value = false
    when (val res = engine.executeSacrifice()) {
      is TriplePlayResult.Success -> {
        _state.value = res.state
        _statusMessage.value = "Cross-board sacrifice activated: +1 extra move!"
      }
      is TriplePlayResult.Rejected -> _statusMessage.value = res.reason
    }
  }

  fun requestVetoDialog() {
    if (engine.canVetoOverride()) {
      _showVetoDialog.value = true
    } else {
      _statusMessage.value = "Veto override requires ${_config.value.vetoCost} stones."
    }
  }

  fun dismissVetoDialog() {
    _showVetoDialog.value = false
  }

  fun confirmVetoOverride() {
    _showVetoDialog.value = false
    when (val res = engine.executeVetoOverride()) {
      is TriplePlayResult.Success -> {
        _state.value = res.state
        _statusMessage.value = "Veto override complete: role handed off & board revealed."
      }
      is TriplePlayResult.Rejected -> _statusMessage.value = res.reason
    }
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  private fun applyMove(boardIndex: Int, move: Move) {
    when (val res = engine.playMove(boardIndex, move)) {
      is TriplePlayResult.Rejected -> _statusMessage.value = res.reason
      is TriplePlayResult.Success -> {
        _state.value = res.state
        if (res.bonusAwarded > 0) {
          _statusMessage.value = "Triple Synergy! +${res.bonusAwarded} stones credited to pool."
        }
        maybeTriggerBot()
      }
    }
  }

  private fun maybeTriggerBot() {
    val cfg = _config.value
    val st = _state.value
    if (cfg.mode != GameMode.HUMAN_VS_BOT || st.isGameOver || st.currentTurn != Team.WHITE) return

    botJob?.cancel()
    _isBotThinking.value = true
    botJob =
      scope.launch {
        val choice = TripleMctsBot.getMove(engine)
        _isBotThinking.value = false
        applyMove(choice.boardIndex, choice.move)
      }
  }
}
