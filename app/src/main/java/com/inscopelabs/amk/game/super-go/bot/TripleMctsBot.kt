package com.inscopelabs.amk.game.supergo.bot

import com.inscopelabs.amk.game.standardgo.bot.MctsBot
import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.supergo.engine.TripleEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class TripleBotChoice(
  val boardIndex: Int,
  val move: Move,
)

object TripleMctsBot {
  suspend fun getMove(tripleEngine: TripleEngine): TripleBotChoice =
    withContext(Dispatchers.Default) {
      val sims = tripleEngine.config.botSimulations.coerceAtLeast(36)
      val candidates =
        (0..2)
          .map { boardIdx ->
            async {
              val cloned = tripleEngine.cloneBoardForBot(boardIdx)
              val move = MctsBot.getMove(cloned, simulations = sims)
              boardIdx to move
            }
          }
          .awaitAll()

      val nonPass = candidates.filter { it.second.isPlacement }
      if (nonPass.isEmpty()) {
        return@withContext TripleBotChoice(0, Move.pass())
      }

      // Optional Sun Tzu feint: occasionally choose a non-active board probe
      val selected =
        if (tripleEngine.config.sunTzu.feint && nonPass.size > 1 && Random.nextFloat() < 0.3f) {
          nonPass.random()
        } else {
          val active = tripleEngine.snapshot().activeBoardIndex
          nonPass.firstOrNull { it.first == active } ?: nonPass.first()
        }

      TripleBotChoice(boardIndex = selected.first, move = selected.second)
    }
}
