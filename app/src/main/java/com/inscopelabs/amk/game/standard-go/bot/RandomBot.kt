package com.inscopelabs.amk.game.standardgo.bot

import com.inscopelabs.amk.game.standardgo.engine.Engine
import com.inscopelabs.amk.game.standardgo.engine.Move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

object RandomBot {
  suspend fun getMove(engine: Engine, random: Random = Random.Default): Move =
    withContext(Dispatchers.Default) {
      val legal = engine.legalMoves(avoidSimpleEyes = true)
      if (legal.isEmpty()) Move.pass() else legal[random.nextInt(legal.size)]
    }
}
