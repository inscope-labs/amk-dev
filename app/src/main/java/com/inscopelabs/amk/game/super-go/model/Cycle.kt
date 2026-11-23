package com.inscopelabs.amk.game.supergo.model

import com.inscopelabs.amk.game.supergo.engine.Role

data class Cycle(
  val turnCount: Int = 0,
  val cycleNumber: Int = 1,
  val phase: Role = Role.OPENER,
) {
  fun advance(turnsPerPhase: Int = 6): Cycle {
    val nextTurn = turnCount + 1
    val nextCycle = (nextTurn / 3) + 1
    val phaseIndex = (nextCycle - 1) / turnsPerPhase
    val nextPhase =
      when {
        phaseIndex == 0 -> Role.OPENER
        phaseIndex == 1 -> Role.MIDFIELDER
        else -> Role.CLOSER
      }
    return Cycle(
      turnCount = nextTurn,
      cycleNumber = nextCycle,
      phase = nextPhase,
    )
  }
}
