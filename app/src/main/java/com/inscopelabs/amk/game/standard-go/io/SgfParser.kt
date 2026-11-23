package com.inscopelabs.amk.game.standardgo.io

import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.Team

data class SgfGame(
  val config: GameConfig,
  val moves: List<Pair<Team, Move>>,
)

object SgfParser {
  fun export(config: GameConfig, moves: List<Pair<Team, Move>>): String = buildString {
    append("(;GM[1]FF[4]SZ[${config.boardSize}]KM[${config.komi}]")
    for ((team, move) in moves) {
      if (move.isResign) continue
      val color = if (team == Team.BLACK) "B" else "W"
      val coord =
        if (move.isPass) {
          ""
        } else {
          val colChar = ('a' + move.col)
          val rowChar = ('a' + move.row)
          "$colChar$rowChar"
        }
      append(";$color[$coord]")
    }
    append(")")
  }

  fun import(sgf: String): SgfGame? {
    val sizeMatch = Regex("""SZ\[(\d+)]""").find(sgf)
    val boardSize = sizeMatch?.groupValues?.get(1)?.toIntOrNull()?.takeIf { it in listOf(9, 13, 19) } ?: 9
    val komiMatch = Regex("""KM\[([0-9.]+)]""").find(sgf)
    val komi = komiMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 6.5

    val moveRegex = Regex(""";([BW])\[([a-s]{0,2})]""")
    val moves =
      moveRegex.findAll(sgf).mapNotNull { match ->
        val team = if (match.groupValues[1] == "B") Team.BLACK else Team.WHITE
        val coord = match.groupValues[2]
        if (coord.isEmpty()) {
          team to Move.pass()
        } else if (coord.length == 2) {
          val col = coord[0] - 'a'
          val row = coord[1] - 'a'
          if (row in 0 until boardSize && col in 0 until boardSize) {
            team to Move.place(row, col)
          } else {
            null
          }
        } else {
          null
        }
      }.toList()

    return SgfGame(
      config = GameConfig(boardSize = boardSize, komi = komi),
      moves = moves,
    )
  }
}
