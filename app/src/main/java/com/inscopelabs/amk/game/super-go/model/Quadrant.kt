package com.inscopelabs.amk.game.supergo.model

enum class Quadrant(val label: String) {
  NW("NW"),
  NE("NE"),
  SW("SW"),
  SE("SE");

  companion object {
    fun fromCoordinate(row: Int, col: Int, boardSize: Int): Quadrant {
      val mid = boardSize / 2
      val isNorth = row < mid
      val isWest = col < mid
      return when {
        isNorth && isWest -> NW
        isNorth && !isWest -> NE
        !isNorth && isWest -> SW
        else -> SE
      }
    }
  }
}
