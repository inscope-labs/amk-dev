package com.inscopelabs.amk.game.supergo.engine

import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.model.Quadrant

data class FogState(
  val enabled: Boolean = true,
  val blackRevealed: Map<Int, Set<Quadrant>> = emptyMap(),
  val whiteRevealed: Map<Int, Set<Quadrant>> = emptyMap(),
) {
  fun revealedFor(team: Team, boardIndex: Int): Set<Quadrant> {
    if (!enabled) return Quadrant.entries.toSet()
    val map = if (team == Team.BLACK) blackRevealed else whiteRevealed
    return map[boardIndex].orEmpty()
  }

  fun isQuadrantRevealed(team: Team, boardIndex: Int, quadrant: Quadrant): Boolean =
    !enabled || quadrant in revealedFor(team, boardIndex)

  fun revealAt(team: Team, boardIndex: Int, row: Int, col: Int, boardSize: Int): FogState {
    if (!enabled) return this
    val quadrant = Quadrant.fromCoordinate(row, col, boardSize)
    return if (team == Team.BLACK) {
      val updated = blackRevealed.toMutableMap()
      updated[boardIndex] = updated[boardIndex].orEmpty() + quadrant
      copy(blackRevealed = updated)
    } else {
      val updated = whiteRevealed.toMutableMap()
      updated[boardIndex] = updated[boardIndex].orEmpty() + quadrant
      copy(whiteRevealed = updated)
    }
  }

  fun revealAllForBoard(team: Team, boardIndex: Int): FogState =
    if (team == Team.BLACK) {
      copy(blackRevealed = blackRevealed + (boardIndex to Quadrant.entries.toSet()))
    } else {
      copy(whiteRevealed = whiteRevealed + (boardIndex to Quadrant.entries.toSet()))
    }
}
