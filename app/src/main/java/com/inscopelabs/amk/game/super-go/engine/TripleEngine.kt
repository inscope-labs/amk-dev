package com.inscopelabs.amk.game.supergo.engine

import com.inscopelabs.amk.game.standardgo.engine.Engine
import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.engine.PlayResult
import com.inscopelabs.amk.game.standardgo.engine.ScoreResult
import com.inscopelabs.amk.game.standardgo.model.GameState
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.model.Cycle
import com.inscopelabs.amk.game.supergo.model.SuperGoConfig

data class TripleGameState(
  val boards: List<GameState>,
  val activeBoardIndex: Int,
  val currentTurn: Team,
  val resourcePool: ResourcePool,
  val fogState: FogState,
  val roleState: RoleState,
  val synergyTracker: SynergyTracker,
  val cycle: Cycle,
  val extraMovesRemaining: Int = 0,
  val blackSacrificeCooldownUntilCycle: Int = 0,
  val whiteSacrificeCooldownUntilCycle: Int = 0,
  val isGameOver: Boolean = false,
  val winner: Team? = null,
  val gameOverReason: String? = null,
  val boardScores: List<ScoreResult> = emptyList(),
)

sealed class TriplePlayResult {
  data class Success(val state: TripleGameState, val bonusAwarded: Int) : TriplePlayResult()
  data class Rejected(val reason: String) : TriplePlayResult()
}

class TripleEngine(val config: SuperGoConfig = SuperGoConfig()) {
  private val engines = List(3) { Engine(config) }
  private var activeBoardIndex: Int = 0
  private var currentTurn: Team = Team.BLACK
  private var resourcePool =
    ResourcePool(
      blackStones = config.poolSizePerPlayer,
      whiteStones = config.poolSizePerPlayer,
    )
  private var fogState = FogState(enabled = config.fogEnabled)
  private var roleState = RoleState()
  private var synergyTracker = SynergyTracker()
  private var cycle = Cycle()
  private var extraMovesRemaining: Int = 0
  private var blackSacrificeCooldownUntilCycle: Int = 0
  private var whiteSacrificeCooldownUntilCycle: Int = 0
  private var isGameOver: Boolean = false
  private var winner: Team? = null
  private var gameOverReason: String? = null
  private var boardScores: List<ScoreResult> = emptyList()
  private var consecutiveGlobalPasses: Int = 0

  fun getBoardEngine(index: Int): Engine = engines[index]

  fun selectBoard(index: Int) {
    if (index in 0..2) {
      activeBoardIndex = index
    }
  }

  fun snapshot(): TripleGameState =
    TripleGameState(
      boards = engines.map { it.snapshot() },
      activeBoardIndex = activeBoardIndex,
      currentTurn = currentTurn,
      resourcePool = resourcePool,
      fogState = fogState,
      roleState = roleState,
      synergyTracker = synergyTracker,
      cycle = cycle,
      extraMovesRemaining = extraMovesRemaining,
      blackSacrificeCooldownUntilCycle = blackSacrificeCooldownUntilCycle,
      whiteSacrificeCooldownUntilCycle = whiteSacrificeCooldownUntilCycle,
      isGameOver = isGameOver,
      winner = winner,
      gameOverReason = gameOverReason,
      boardScores = boardScores,
    )

  fun playMove(boardIndex: Int, move: Move): TriplePlayResult {
    if (isGameOver) return TriplePlayResult.Rejected("Match is already finished.")
    if (boardIndex !in 0..2) return TriplePlayResult.Rejected("Invalid board index.")

    val targetEngine = engines[boardIndex]
    if (targetEngine.isFinished()) {
      return TriplePlayResult.Rejected("Board ${boardIndex + 1} is already closed.")
    }

    if (move.isResign) {
      isGameOver = true
      winner = currentTurn.opponent()
      gameOverReason = "${currentTurn.displayName} resigned the triple-board match."
      boardScores = engines.map { it.currentScoreEstimate() }
      return TriplePlayResult.Success(snapshot(), 0)
    }

    if (move.isPass) {
      consecutiveGlobalPasses++
      if (consecutiveGlobalPasses >= 4) {
        finishByScoring("Both players passed consecutively across boards.")
        return TriplePlayResult.Success(snapshot(), 0)
      }
      advanceTurnAndCycle(boardIndex)
      return TriplePlayResult.Success(snapshot(), 0)
    }

    if (!resourcePool.canAfford(currentTurn, 1)) {
      return TriplePlayResult.Rejected("${currentTurn.displayName} has 0 stones in pool — pass or suffer collapse.")
    }

    // Align board engine turn with shared triple turn
    targetEngine.forceTurn(currentTurn)
    when (val res = targetEngine.playMove(move)) {
      is PlayResult.Rejected -> return TriplePlayResult.Rejected(res.reason)
      is PlayResult.Success -> {
        consecutiveGlobalPasses = 0
        resourcePool = resourcePool.debit(currentTurn, 1)
        fogState = fogState.revealAt(currentTurn, boardIndex, move.row, move.col, config.boardSize)

        var bonus = 0
        if (res.capturedCount > 0) {
          // Refund captured stones + check triple synergy
          val (nextSynergy, tripleBonus) =
            synergyTracker.onCapture(currentTurn, boardIndex, res.capturedCount, cycle.cycleNumber)
          synergyTracker = nextSynergy
          bonus = tripleBonus
          val captureCredit = res.capturedCount + tripleBonus + (if (config.sunTzu.supplyDepot) 2 else 0)
          resourcePool = resourcePool.credit(currentTurn, captureCredit)
        }

        roleState = roleState.recordMove(currentTurn)
        activeBoardIndex = boardIndex

        if (extraMovesRemaining > 0) {
          extraMovesRemaining--
        } else {
          advanceTurnAndCycle(boardIndex)
        }

        return TriplePlayResult.Success(snapshot(), bonus)
      }
    }
  }

  fun canSacrifice(team: Team = currentTurn): Boolean {
    if (isGameOver) return false
    val cooldownUntil =
      if (team == Team.BLACK) blackSacrificeCooldownUntilCycle else whiteSacrificeCooldownUntilCycle
    return cycle.cycleNumber >= cooldownUntil && resourcePool.canAfford(team, config.sacrificeCost)
  }

  fun executeSacrifice(): TriplePlayResult {
    if (!canSacrifice(currentTurn)) {
      return TriplePlayResult.Rejected("Sacrifice unavailable: insufficient stones or on cooldown.")
    }
    resourcePool = resourcePool.debit(currentTurn, config.sacrificeCost)
    extraMovesRemaining += 1
    if (currentTurn == Team.BLACK) {
      blackSacrificeCooldownUntilCycle = cycle.cycleNumber + config.sacrificeCooldownCycles
    } else {
      whiteSacrificeCooldownUntilCycle = cycle.cycleNumber + config.sacrificeCooldownCycles
    }
    return TriplePlayResult.Success(snapshot(), 0)
  }

  fun canVetoOverride(team: Team = currentTurn): Boolean =
    !isGameOver && resourcePool.canAfford(team, config.vetoCost)

  fun executeVetoOverride(): TriplePlayResult {
    if (!canVetoOverride(currentTurn)) {
      return TriplePlayResult.Rejected("Veto override requires ${config.vetoCost} stones.")
    }
    resourcePool = resourcePool.debit(currentTurn, config.vetoCost)
    roleState = roleState.handOff(currentTurn)
    fogState = fogState.revealAllForBoard(currentTurn, activeBoardIndex)
    return TriplePlayResult.Success(snapshot(), 0)
  }

  fun handOffRole(): TripleGameState {
    roleState = roleState.handOff(currentTurn)
    return snapshot()
  }

  fun cloneBoardForBot(index: Int): Engine {
    val cloned = engines[index].cloneForSimulation()
    cloned.forceTurn(currentTurn)
    return cloned
  }

  private fun advanceTurnAndCycle(playedBoardIndex: Int) {
    val (updatedPool, collapsed) = resourcePool.recordRotation(currentTurn)
    resourcePool = updatedPool
    if (collapsed) {
      isGameOver = true
      winner = currentTurn.opponent()
      gameOverReason = "${currentTurn.displayName} suffered Resource Collapse (3 rotations at 0 stones)."
      boardScores = engines.map { it.currentScoreEstimate() }
      return
    }

    currentTurn = currentTurn.opponent()
    cycle = cycle.advance()
    activeBoardIndex = (playedBoardIndex + 1) % 3
  }

  private fun finishByScoring(reason: String) {
    isGameOver = true
    boardScores = engines.map { it.currentScoreEstimate() }
    val blackWins = boardScores.count { it.winner == Team.BLACK }
    val whiteWins = boardScores.count { it.winner == Team.WHITE }
    winner = if (blackWins > whiteWins) Team.BLACK else Team.WHITE
    gameOverReason = "$reason (Boards: Black $blackWins – White $whiteWins)"
  }
}
