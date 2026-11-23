package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

data class Group(
  val team: Team,
  val stones: Set<Pair<Int, Int>>,
  val liberties: Set<Pair<Int, Int>>,
) {
  val libertyCount: Int
    get() = liberties.size
}
