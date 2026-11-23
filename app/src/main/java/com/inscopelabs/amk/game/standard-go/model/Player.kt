package com.inscopelabs.amk.game.standardgo.model

enum class PlayerType {
  HUMAN,
  RANDOM_BOT,
  MCTS_BOT,
}

data class Player(
  val team: Team,
  val name: String,
  val type: PlayerType = PlayerType.HUMAN,
) {
  val isBot: Boolean
    get() = type != PlayerType.HUMAN
}
