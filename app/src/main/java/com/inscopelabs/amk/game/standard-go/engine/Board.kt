package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

data class PlacementOutcome(
  val nextBoard: Board,
  val capturedStones: List<Pair<Int, Int>>,
  val selfLiberties: Int,
)

class Board(
  val size: Int,
  private val cells: IntArray = IntArray(size * size),
) {
  fun inBounds(row: Int, col: Int): Boolean = row in 0 until size && col in 0 until size

  fun getRaw(row: Int, col: Int): Int {
    if (!inBounds(row, col)) return -1
    return cells[row * size + col]
  }

  fun getTeam(row: Int, col: Int): Team? = Team.fromValue(getRaw(row, col))

  fun isEmpty(row: Int, col: Int): Boolean = getRaw(row, col) == 0

  fun setTeam(row: Int, col: Int, team: Team?) {
    if (inBounds(row, col)) {
      cells[row * size + col] = team?.value ?: 0
    }
  }

  fun setRaw(row: Int, col: Int, value: Int) {
    if (inBounds(row, col)) {
      cells[row * size + col] = value
    }
  }

  fun copy(): Board = Board(size, cells.copyOf())

  fun toList(): List<Int> = cells.toList()

  fun neighbors(row: Int, col: Int): List<Pair<Int, Int>> {
    val n = size
    return buildList(4) {
      if (row > 0) add((row - 1) to col)
      if (row < n - 1) add((row + 1) to col)
      if (col > 0) add(row to (col - 1))
      if (col < n - 1) add(row to (col + 1))
    }
  }

  fun tryPlaceStone(row: Int, col: Int, team: Team): PlacementOutcome? {
    if (!inBounds(row, col) || !isEmpty(row, col)) return null

    val next = copy()
    next.setTeam(row, col, team)

    val opponentVal = team.opponent().value
    val captured = LinkedHashSet<Pair<Int, Int>>()

    for ((nr, nc) in next.neighbors(row, col)) {
      if (next.getRaw(nr, nc) == opponentVal && (nr to nc) !in captured) {
        val oppGroup = Liberty.findGroup(next, nr, nc)
        if (oppGroup != null && oppGroup.libertyCount == 0) {
          for (stone in oppGroup.stones) {
            next.setTeam(stone.first, stone.second, null)
            captured.add(stone)
          }
        }
      }
    }

    val ownGroup = Liberty.findGroup(next, row, col)
    val selfLibs = ownGroup?.libertyCount ?: 0

    return PlacementOutcome(
      nextBoard = next,
      capturedStones = captured.toList(),
      selfLiberties = selfLibs,
    )
  }

  fun stoneCount(team: Team): Int = cells.count { it == team.value }

  companion object {
    fun fromList(size: Int, grid: List<Int>): Board {
      require(grid.size == size * size) { "Grid size ${grid.size} does not match ${size}x$size" }
      return Board(size, grid.toIntArray())
    }
  }
}
