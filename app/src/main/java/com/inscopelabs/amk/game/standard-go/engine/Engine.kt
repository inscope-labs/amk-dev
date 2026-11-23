package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.GameState
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.util.handicapPoints

sealed class PlayResult {
  data class Success(
    val state: GameState,
    val capturedCount: Int,
    val capturedStones: List<Pair<Int, Int>>,
  ) : PlayResult()

  data class Rejected(val reason: String) : PlayResult()
}

private data class EngineSnapshot(
  val board: Board,
  val currentTurn: Team,
  val blackCaptures: Int,
  val whiteCaptures: Int,
  val consecutivePasses: Int,
  val moveCount: Int,
  val lastMove: Move?,
  val koPoint: Pair<Int, Int>?,
  val boardHistory: Set<Long>,
  val isGameOver: Boolean,
  val resignedBy: Team?,
  val winner: Team?,
  val scoreSummary: ScoreResult?,
)

class Engine(val config: GameConfig = GameConfig()) {
  private var board: Board = Board(config.boardSize)
  private var currentTurn: Team = Team.BLACK
  private var blackCaptures: Int = 0
  private var whiteCaptures: Int = 0
  private var consecutivePasses: Int = 0
  private var moveCount: Int = 0
  private var lastMove: Move? = null
  private var koPoint: Pair<Int, Int>? = null
  private val boardHistory = LinkedHashSet<Long>()
  private var isGameOver: Boolean = false
  private var resignedBy: Team? = null
  private var winner: Team? = null
  private var scoreSummary: ScoreResult? = null

  private val undoStack = ArrayDeque<EngineSnapshot>()
  val moveLog = mutableListOf<Pair<Team, Move>>()

  init {
    reset(config)
  }

  fun reset(newConfig: GameConfig = config) {
    board = Board(newConfig.boardSize)
    blackCaptures = 0
    whiteCaptures = 0
    consecutivePasses = 0
    moveCount = 0
    lastMove = null
    koPoint = null
    boardHistory.clear()
    undoStack.clear()
    moveLog.clear()
    isGameOver = false
    resignedBy = null
    winner = null
    scoreSummary = null

    if (newConfig.handicap >= 2) {
      for ((r, c) in handicapPoints(newConfig.boardSize, newConfig.handicap)) {
        board.setTeam(r, c, Team.BLACK)
      }
      currentTurn = Team.WHITE
    } else {
      currentTurn = Team.BLACK
    }

    boardHistory.add(Zobrist.hashBoardOnly(board))
  }

  fun currentBoard(): Board = board.copy()

  fun currentTeam(): Team = currentTurn

  fun isFinished(): Boolean = isGameOver

  fun snapshot(): GameState =
    GameState(
      boardSize = board.size,
      grid = board.toList(),
      currentTurn = currentTurn,
      blackCaptures = blackCaptures,
      whiteCaptures = whiteCaptures,
      consecutivePasses = consecutivePasses,
      moveCount = moveCount,
      lastMove = lastMove,
      koPoint = koPoint,
      positionHash = Zobrist.hashPosition(board, currentTurn),
      isGameOver = isGameOver,
      resignedBy = resignedBy,
      winner = winner,
      scoreSummary = scoreSummary,
      canUndo = undoStack.isNotEmpty(),
    )

  fun playMove(move: Move): PlayResult {
    if (isGameOver) {
      return PlayResult.Rejected("Game is already over.")
    }

    if (move.isResign) {
      return resign()
    }
    if (move.isPass) {
      return pass()
    }

    return when (val validation = Rules.validateMove(board, move.row, move.col, currentTurn, koPoint, boardHistory)) {
      is MoveValidation.Invalid -> PlayResult.Rejected(validation.reason)
      is MoveValidation.Valid -> {
        saveSnapshot()
        val captured = validation.outcome.capturedStones
        if (currentTurn == Team.BLACK) {
          blackCaptures += captured.size
        } else {
          whiteCaptures += captured.size
        }
        board = validation.outcome.nextBoard
        koPoint = validation.nextKoPoint
        boardHistory.add(validation.nextHash)
        moveLog.add(currentTurn to move)
        lastMove = move
        moveCount += 1
        consecutivePasses = 0
        currentTurn = currentTurn.opponent()

        PlayResult.Success(
          state = snapshot(),
          capturedCount = captured.size,
          capturedStones = captured,
        )
      }
    }
  }

  fun pass(): PlayResult {
    if (isGameOver) {
      return PlayResult.Rejected("Game is already over.")
    }
    saveSnapshot()
    val passMove = Move.pass()
    moveLog.add(currentTurn to passMove)
    lastMove = passMove
    moveCount += 1
    consecutivePasses += 1
    koPoint = null
    currentTurn = currentTurn.opponent()

    if (consecutivePasses >= 2) {
      val score = Score.calculate(board, blackCaptures, whiteCaptures, config.komi, config.ruleset)
      scoreSummary = score
      winner = score.winner
      isGameOver = true
    }

    return PlayResult.Success(
      state = snapshot(),
      capturedCount = 0,
      capturedStones = emptyList(),
    )
  }

  fun resign(): PlayResult {
    if (isGameOver) {
      return PlayResult.Rejected("Game is already over.")
    }
    saveSnapshot()
    val resignMove = Move.resign()
    moveLog.add(currentTurn to resignMove)
    resignedBy = currentTurn
    winner = currentTurn.opponent()
    isGameOver = true
    lastMove = resignMove
    moveCount += 1

    return PlayResult.Success(
      state = snapshot(),
      capturedCount = 0,
      capturedStones = emptyList(),
    )
  }

  fun undo(): GameState? {
    val prev = undoStack.removeLastOrNull() ?: return null
    board = prev.board
    currentTurn = prev.currentTurn
    blackCaptures = prev.blackCaptures
    whiteCaptures = prev.whiteCaptures
    consecutivePasses = prev.consecutivePasses
    moveCount = prev.moveCount
    lastMove = prev.lastMove
    koPoint = prev.koPoint
    boardHistory.clear()
    boardHistory.addAll(prev.boardHistory)
    isGameOver = prev.isGameOver
    resignedBy = prev.resignedBy
    winner = prev.winner
    scoreSummary = prev.scoreSummary
    if (moveLog.isNotEmpty()) {
      moveLog.removeAt(moveLog.lastIndex)
    }
    return snapshot()
  }

  fun legalMoves(avoidSimpleEyes: Boolean = false): List<Move> {
    if (isGameOver) return emptyList()
    val moves = ArrayList<Move>()
    for (r in 0 until board.size) {
      for (c in 0 until board.size) {
        if (!board.isEmpty(r, c)) continue
        if (avoidSimpleEyes && isLikelyOwnEye(r, c, currentTurn)) continue
        val v = Rules.validateMove(board, r, c, currentTurn, koPoint, boardHistory)
        if (v is MoveValidation.Valid) {
          moves.add(Move.place(r, c))
        }
      }
    }
    return moves
  }

  fun currentScoreEstimate(): ScoreResult =
    Score.calculate(board, blackCaptures, whiteCaptures, config.komi, config.ruleset)

  fun cloneForSimulation(): Engine {
    val copy = Engine(config)
    copy.board = board.copy()
    copy.currentTurn = currentTurn
    copy.blackCaptures = blackCaptures
    copy.whiteCaptures = whiteCaptures
    copy.consecutivePasses = consecutivePasses
    copy.moveCount = moveCount
    copy.lastMove = lastMove
    copy.koPoint = koPoint
    copy.boardHistory.clear()
    copy.boardHistory.addAll(boardHistory)
    copy.isGameOver = isGameOver
    copy.resignedBy = resignedBy
    copy.winner = winner
    copy.scoreSummary = scoreSummary
    return copy
  }

  fun restoreFromState(state: GameState) {
    board = Board.fromList(state.boardSize, state.grid)
    currentTurn = state.currentTurn
    blackCaptures = state.blackCaptures
    whiteCaptures = state.whiteCaptures
    consecutivePasses = state.consecutivePasses
    moveCount = state.moveCount
    lastMove = state.lastMove
    koPoint = state.koPoint
    boardHistory.clear()
    boardHistory.add(Zobrist.hashBoardOnly(board))
    isGameOver = state.isGameOver
    resignedBy = state.resignedBy
    winner = state.winner
    scoreSummary = state.scoreSummary
    undoStack.clear()
  }

  fun forceTurn(nextTurn: Team) {
    currentTurn = nextTurn
  }

  fun forceGameOver(winningTeam: Team) {
    isGameOver = true
    winner = winningTeam
  }

  private fun isLikelyOwnEye(row: Int, col: Int, team: Team): Boolean {
    val neighbors = board.neighbors(row, col)
    if (neighbors.any { (nr, nc) -> board.getTeam(nr, nc) != team }) return false
    var offBoard = 0
    var enemyDiag = 0
    val diags = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
    for ((dr, dc) in diags) {
      val nr = row + dr
      val nc = col + dc
      if (!board.inBounds(nr, nc)) {
        offBoard++
      } else if (board.getTeam(nr, nc) == team.opponent()) {
        enemyDiag++
      }
    }
    return if (offBoard > 0) enemyDiag == 0 else enemyDiag <= 1
  }

  private fun saveSnapshot() {
    undoStack.addLast(
      EngineSnapshot(
        board = board.copy(),
        currentTurn = currentTurn,
        blackCaptures = blackCaptures,
        whiteCaptures = whiteCaptures,
        consecutivePasses = consecutivePasses,
        moveCount = moveCount,
        lastMove = lastMove,
        koPoint = koPoint,
        boardHistory = HashSet(boardHistory),
        isGameOver = isGameOver,
        resignedBy = resignedBy,
        winner = winner,
        scoreSummary = scoreSummary,
      )
    )
  }
}
