package com.inscopelabs.amk.game.standardgo.util

private const val GTP_LETTERS = "ABCDEFGHJKLMNOPQRST"

fun Int.clampBoard(min: Int, max: Int): Int = coerceIn(min, max)

fun formatGtpCoordinate(row: Int, col: Int, boardSize: Int): String {
  if (row !in 0 until boardSize || col !in 0 until boardSize) return "PASS"
  val letter = GTP_LETTERS.getOrElse(col) { '?' }
  val number = boardSize - row
  return "$letter$number"
}

fun gtpColumnLabels(boardSize: Int): List<String> =
  (0 until boardSize).map { GTP_LETTERS[it].toString() }

fun hoshiPoints(boardSize: Int): List<Pair<Int, Int>> =
  when (boardSize) {
    9 ->
      listOf(
        2 to 2, 2 to 6,
        4 to 4,
        6 to 2, 6 to 6,
      )
    13 ->
      listOf(
        3 to 3, 3 to 9,
        6 to 6,
        9 to 3, 9 to 9,
      )
    19 ->
      listOf(
        3 to 3, 3 to 9, 3 to 15,
        9 to 3, 9 to 9, 9 to 15,
        15 to 3, 15 to 9, 15 to 15,
      )
    else -> emptyList()
  }

fun handicapPoints(boardSize: Int, handicap: Int): List<Pair<Int, Int>> {
  if (handicap < 2) return emptyList()
  val edge = if (boardSize == 9) 2 else 3
  val far = boardSize - 1 - edge
  val mid = boardSize / 2
  val order =
    listOf(
      edge to far,
      far to edge,
      far to far,
      edge to edge,
      mid to mid,
      mid to edge,
      mid to far,
      edge to mid,
      far to mid,
    )
  return order.take(handicap.coerceAtMost(order.size))
}
