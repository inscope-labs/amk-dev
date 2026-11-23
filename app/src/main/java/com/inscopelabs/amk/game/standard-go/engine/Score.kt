package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.Ruleset
import com.inscopelabs.amk.game.standardgo.model.Team
import kotlin.math.abs

data class ScoreResult(
  val blackScore: Double,
  val whiteScore: Double,
  val blackTerritory: Int,
  val whiteTerritory: Int,
  val blackStones: Int,
  val whiteStones: Int,
  val blackCaptures: Int,
  val whiteCaptures: Int,
  val komi: Double,
  val ruleset: Ruleset,
  val winner: Team,
  val margin: Double,
)

object Score {
  fun calculate(
    board: Board,
    blackCaptures: Int,
    whiteCaptures: Int,
    komi: Double,
    ruleset: Ruleset,
  ): ScoreResult {
    val n = board.size
    val visited = BooleanArray(n * n)
    var blackTerritory = 0
    var whiteTerritory = 0

    for (r in 0 until n) {
      for (c in 0 until n) {
        val idx = r * n + c
        if (!visited[idx] && board.isEmpty(r, c)) {
          val region = ArrayList<Pair<Int, Int>>()
          val queue = ArrayDeque<Pair<Int, Int>>()
          visited[idx] = true
          queue.addLast(r to c)

          var touchesBlack = false
          var touchesWhite = false

          while (queue.isNotEmpty()) {
            val (cr, cc) = queue.removeFirst()
            region.add(cr to cc)
            for ((nr, nc) in board.neighbors(cr, cc)) {
              val valAt = board.getRaw(nr, nc)
              when (valAt) {
                0 -> {
                  val nIdx = nr * n + nc
                  if (!visited[nIdx]) {
                    visited[nIdx] = true
                    queue.addLast(nr to nc)
                  }
                }
                Team.BLACK.value -> touchesBlack = true
                Team.WHITE.value -> touchesWhite = true
              }
            }
          }

          if (touchesBlack && !touchesWhite) {
            blackTerritory += region.size
          } else if (touchesWhite && !touchesBlack) {
            whiteTerritory += region.size
          }
        }
      }
    }

    val blackStones = board.stoneCount(Team.BLACK)
    val whiteStones = board.stoneCount(Team.WHITE)

    val blackTotal: Double
    val whiteTotal: Double

    when (ruleset) {
      Ruleset.JAPANESE -> {
        blackTotal = (blackTerritory + blackCaptures).toDouble()
        whiteTotal = whiteTerritory + whiteCaptures + komi
      }
      Ruleset.CHINESE -> {
        blackTotal = (blackTerritory + blackStones).toDouble()
        whiteTotal = whiteTerritory + whiteStones + komi
      }
    }

    val winner = if (blackTotal > whiteTotal) Team.BLACK else Team.WHITE
    val margin = abs(blackTotal - whiteTotal)

    return ScoreResult(
      blackScore = blackTotal,
      whiteScore = whiteTotal,
      blackTerritory = blackTerritory,
      whiteTerritory = whiteTerritory,
      blackStones = blackStones,
      whiteStones = whiteStones,
      blackCaptures = blackCaptures,
      whiteCaptures = whiteCaptures,
      komi = komi,
      ruleset = ruleset,
      winner = winner,
      margin = margin,
    )
  }
}
