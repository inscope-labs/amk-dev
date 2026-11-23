package com.inscopelabs.amk.game.standardgo.model

import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.engine.ScoreResult

data class GameState(
  val boardSize: Int,
  val grid: List<Int>,
  val currentTurn: Team,
  val blackCaptures: Int,
  val whiteCaptures: Int,
  val consecutivePasses: Int,
  val moveCount: Int,
  val lastMove: Move?,
  val koPoint: Pair<Int, Int>?,
  val positionHash: Long,
  val isGameOver: Boolean,
  val resignedBy: Team? = null,
  val winner: Team? = null,
  val scoreSummary: ScoreResult? = null,
  val canUndo: Boolean = false,
) {
  fun cellAt(row: Int, col: Int): Int = grid[row * boardSize + col]
}
