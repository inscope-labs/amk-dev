package com.inscopelabs.amk.game.supergo.model

import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.PlayerType
import com.inscopelabs.amk.game.standardgo.model.Ruleset
import com.inscopelabs.amk.game.supergo.engine.SunTzuConfig

class SuperGoConfig(
  boardSize: Int = 9,
  komi: Double = 6.5,
  ruleset: Ruleset = Ruleset.JAPANESE,
  handicap: Int = 0,
  mode: GameMode = GameMode.HUMAN_VS_BOT,
  botType: PlayerType = PlayerType.MCTS_BOT,
  botSimulations: Int = 120,
  val poolSizePerPlayer: Int = 60,
  val fogEnabled: Boolean = true,
  val sacrificeCost: Int = 10,
  val sacrificeCooldownCycles: Int = 3,
  val vetoCost: Int = 2,
  val sunTzu: SunTzuConfig = SunTzuConfig(),
) : GameConfig(
    boardSize = boardSize,
    komi = komi,
    ruleset = ruleset,
    handicap = handicap,
    mode = mode,
    botType = botType,
    botSimulations = botSimulations,
  )
