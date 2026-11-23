package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.Team
import kotlin.random.Random

object Zobrist {
  private const val MAX_SIZE = 19
  private val table: Array<LongArray>
  private val turnKeys: LongArray

  init {
    val rng = Random(0x5A0B2157CAFEBABEL)
    table = Array(MAX_SIZE * MAX_SIZE) { LongArray(2) { rng.nextLong() } }
    turnKeys = longArrayOf(rng.nextLong(), rng.nextLong())
  }

  fun hashBoardOnly(board: Board): Long {
    var h = 0L
    val n = board.size
    for (r in 0 until n) {
      for (c in 0 until n) {
        val v = board.getRaw(r, c)
        if (v == 1 || v == 2) {
          h = h xor table[r * MAX_SIZE + c][v - 1]
        }
      }
    }
    return h
  }

  fun hashPosition(board: Board, turn: Team): Long =
    hashBoardOnly(board) xor turnKeys[turn.value - 1]
}
