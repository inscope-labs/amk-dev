package com.inscopelabs.amk.game.supergo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

data class SynergyEvent(
  val team: Team,
  val boardIndex: Int,
  val cycleNumber: Int,
  val awardedTripleBonus: Boolean,
)

data class SynergyTracker(
  val blackCaptureBoards: Set<Int> = emptySet(),
  val whiteCaptureBoards: Set<Int> = emptySet(),
  val blackTripleCount: Int = 0,
  val whiteTripleCount: Int = 0,
  val recentLog: List<String> = emptyList(),
) {
  companion object {
    const val TRIPLE_SYNERGY_BONUS = 15
  }

  fun boardsCapturedFor(team: Team): Set<Int> =
    if (team == Team.BLACK) blackCaptureBoards else whiteCaptureBoards

  fun tripleCountFor(team: Team): Int =
    if (team == Team.BLACK) blackTripleCount else whiteTripleCount

  fun onCapture(
    team: Team,
    boardIndex: Int,
    capturedCount: Int,
    cycleNumber: Int,
  ): Pair<SynergyTracker, Int> {
    if (capturedCount <= 0) return this to 0

    val currentSet = boardsCapturedFor(team) + boardIndex
    val triggeredTriple = currentSet.size >= 3
    val bonus = if (triggeredTriple) TRIPLE_SYNERGY_BONUS else 0
    val nextSet = if (triggeredTriple) emptySet() else currentSet

    val entry =
      if (triggeredTriple) {
        "Cycle $cycleNumber: ${team.displayName} achieved TRIPLE SYNERGY (+$TRIPLE_SYNERGY_BONUS stones!)"
      } else {
        "Cycle $cycleNumber: ${team.displayName} captured $capturedCount on Board ${boardIndex + 1}"
      }
    val nextLog = (listOf(entry) + recentLog).take(8)

    val updated =
      if (team == Team.BLACK) {
        copy(
          blackCaptureBoards = nextSet,
          blackTripleCount = blackTripleCount + (if (triggeredTriple) 1 else 0),
          recentLog = nextLog,
        )
      } else {
        copy(
          whiteCaptureBoards = nextSet,
          whiteTripleCount = whiteTripleCount + (if (triggeredTriple) 1 else 0),
          recentLog = nextLog,
        )
      }

    return updated to bonus
  }
}
