package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

object Liberty {
  fun findGroup(board: Board, startRow: Int, startCol: Int): Group? {
    val teamValue = board.getRaw(startRow, startCol)
    val team = Team.fromValue(teamValue) ?: return null

    val stones = LinkedHashSet<Pair<Int, Int>>()
    val liberties = LinkedHashSet<Pair<Int, Int>>()
    val queue = ArrayDeque<Pair<Int, Int>>()
    val start = startRow to startCol

    stones.add(start)
    queue.addLast(start)

    while (queue.isNotEmpty()) {
      val (r, c) = queue.removeFirst()
      for ((nr, nc) in board.neighbors(r, c)) {
        val neighborVal = board.getRaw(nr, nc)
        val coord = nr to nc
        when (neighborVal) {
          0 -> liberties.add(coord)
          teamValue -> {
            if (stones.add(coord)) {
              queue.addLast(coord)
            }
          }
        }
      }
    }

    return Group(team = team, stones = stones, liberties = liberties)
  }

  fun countLiberties(board: Board, row: Int, col: Int): Int =
    findGroup(board, row, col)?.libertyCount ?: 0
}
