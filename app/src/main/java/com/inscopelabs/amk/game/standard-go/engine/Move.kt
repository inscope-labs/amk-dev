package com.inscopelabs.amk.game.standardgo.engine

data class Move(
  val row: Int = -1,
  val col: Int = -1,
  val isPass: Boolean = false,
  val isResign: Boolean = false,
) {
  val isPlacement: Boolean
    get() = !isPass && !isResign && row >= 0 && col >= 0

  companion object {
    fun place(row: Int, col: Int): Move = Move(row = row, col = col)
    fun pass(): Move = Move(isPass = true)
    fun resign(): Move = Move(isResign = true)
  }
}
