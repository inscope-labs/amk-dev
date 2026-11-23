package com.inscopelabs.amk.game.standardgo.model

enum class Ruleset(val label: String) {
  JAPANESE("Japanese (Territory)"),
  CHINESE("Chinese (Area)"),
}

enum class GameMode(val label: String) {
  HUMAN_VS_BOT("vs AI"),
  HUMAN_VS_HUMAN("Pass & Play"),
}

open class GameConfig(
  val boardSize: Int = 9,
  val komi: Double = 6.5,
  val ruleset: Ruleset = Ruleset.JAPANESE,
  val handicap: Int = 0,
  val mode: GameMode = GameMode.HUMAN_VS_BOT,
  val botType: PlayerType = PlayerType.MCTS_BOT,
  val botSimulations: Int = 180,
) {
  init {
    require(boardSize in listOf(9, 13, 19)) { "Supported board sizes are 9, 13, and 19." }
    require(handicap in 0..9) { "Handicap must be between 0 and 9." }
  }
}
