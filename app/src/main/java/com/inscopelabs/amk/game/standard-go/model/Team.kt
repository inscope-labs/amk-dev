package com.inscopelabs.amk.game.standardgo.model

enum class Team(val value: Int, val displayName: String) {
  BLACK(1, "Black"),
  WHITE(2, "White");

  fun opponent(): Team = if (this == BLACK) WHITE else BLACK

  companion object {
    fun fromValue(value: Int): Team? = entries.firstOrNull { it.value == value }
  }
}
