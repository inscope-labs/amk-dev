package com.inscopelabs.amk.game.supergo.engine

enum class Role(
  val title: String,
  val summary: String,
) {
  OPENER("Opener", "Early expansion & quadrant scouting"),
  MIDFIELDER("Midfielder", "Cross-board skirmish & synergy setup"),
  CLOSER("Closer", "Endgame consolidation & yose"),
  ;

  fun next(): Role =
    when (this) {
      OPENER -> MIDFIELDER
      MIDFIELDER -> CLOSER
      CLOSER -> OPENER
    }
}
