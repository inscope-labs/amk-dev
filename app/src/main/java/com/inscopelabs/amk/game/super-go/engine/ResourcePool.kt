package com.inscopelabs.amk.game.supergo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

data class ResourcePool(
  val blackStones: Int = 60,
  val whiteStones: Int = 60,
  val blackZeroRotations: Int = 0,
  val whiteZeroRotations: Int = 0,
) {
  fun stonesFor(team: Team): Int = if (team == Team.BLACK) blackStones else whiteStones

  fun zeroRotationsFor(team: Team): Int = if (team == Team.BLACK) blackZeroRotations else whiteZeroRotations

  fun canAfford(team: Team, cost: Int): Boolean = stonesFor(team) >= cost

  fun debit(team: Team, cost: Int): ResourcePool {
    require(canAfford(team, cost)) { "${team.displayName} stone pool insufficient for cost $cost" }
    return if (team == Team.BLACK) {
      copy(blackStones = blackStones - cost)
    } else {
      copy(whiteStones = whiteStones - cost)
    }
  }

  fun credit(team: Team, amount: Int): ResourcePool {
    if (amount <= 0) return this
    return if (team == Team.BLACK) {
      copy(blackStones = blackStones + amount, blackZeroRotations = 0)
    } else {
      copy(whiteStones = whiteStones + amount, whiteZeroRotations = 0)
    }
  }

  fun recordRotation(team: Team): Pair<ResourcePool, Boolean> {
    val stones = stonesFor(team)
    return if (stones <= 0) {
      val nextStreak = zeroRotationsFor(team) + 1
      val updated =
        if (team == Team.BLACK) copy(blackZeroRotations = nextStreak)
        else copy(whiteZeroRotations = nextStreak)
      updated to (nextStreak >= 3)
    } else {
      val updated =
        if (team == Team.BLACK) copy(blackZeroRotations = 0)
        else copy(whiteZeroRotations = 0)
      updated to false
    }
  }
}
