package com.inscopelabs.amk.game.standardgo.bot

import com.inscopelabs.amk.game.standardgo.engine.Engine
import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.engine.PlayResult
import com.inscopelabs.amk.game.standardgo.model.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

object MctsBot {
  private class Node(
    val move: Move?,
    val parent: Node?,
    val playerJustMoved: Team,
    val untriedMoves: MutableList<Move>,
  ) {
    val children = ArrayList<Node>()
    var visits: Int = 0
    var wins: Double = 0.0

    fun uctSelectChild(exploration: Double = 1.414): Node {
      val logParent = ln(visits.coerceAtLeast(1).toDouble())
      return children.maxBy { child ->
        val exploit = child.wins / child.visits.coerceAtLeast(1)
        val explore = exploration * sqrt(logParent / child.visits.coerceAtLeast(1))
        exploit + explore
      }
    }
  }

  suspend fun getMove(engine: Engine, simulations: Int = 180): Move =
    withContext(Dispatchers.Default) {
      val rootPlayer = engine.currentTeam()
      val legalMoves = engine.legalMoves(avoidSimpleEyes = true)
      if (legalMoves.isEmpty()) {
        return@withContext Move.pass()
      }

      // Limit branching factor slightly on 19x19 openings for fast interactive response
      val candidateMoves =
        if (legalMoves.size > 48) {
          val board = engine.currentBoard()
          val center = board.size / 2
          legalMoves
            .sortedBy { m ->
              val hasNeighbor =
                board.neighbors(m.row, m.col).any { (nr, nc) -> !board.isEmpty(nr, nc) }
              val dist = kotlin.math.abs(m.row - center) + kotlin.math.abs(m.col - center)
              (if (hasNeighbor) 0 else 100) + dist
            }
            .take(48)
        } else {
          legalMoves
        }

      val root =
        Node(
          move = null,
          parent = null,
          playerJustMoved = rootPlayer.opponent(),
          untriedMoves = candidateMoves.toMutableList(),
        )

      val rng = Random.Default
      val playoutLimit = (engine.config.boardSize * engine.config.boardSize).coerceAtMost(90)

      repeat(simulations.coerceAtLeast(24)) {
        coroutineContext.ensureActive()

        var node = root
        val simEngine = engine.cloneForSimulation()

        // 1. Selection
        while (node.untriedMoves.isEmpty() && node.children.isNotEmpty() && !simEngine.isFinished()) {
          node = node.uctSelectChild()
          node.move?.let { simEngine.playMove(it) }
        }

        // 2. Expansion
        if (node.untriedMoves.isNotEmpty() && !simEngine.isFinished()) {
          val idx = rng.nextInt(node.untriedMoves.size)
          val m = node.untriedMoves.removeAt(idx)
          val mover = simEngine.currentTeam()
          val playRes = simEngine.playMove(m)
          val childMoves =
            if (playRes is PlayResult.Success && !simEngine.isFinished()) {
              simEngine.legalMoves(avoidSimpleEyes = true).take(24).toMutableList()
            } else {
              mutableListOf()
            }
          val child =
            Node(
              move = m,
              parent = node,
              playerJustMoved = mover,
              untriedMoves = childMoves,
            )
          node.children.add(child)
          node = child
        }

        // 3. Playout
        var plies = 0
        while (!simEngine.isFinished() && plies < playoutLimit) {
          val moves = simEngine.legalMoves(avoidSimpleEyes = true)
          if (moves.isEmpty()) {
            simEngine.pass()
          } else {
            simEngine.playMove(moves[rng.nextInt(moves.size)])
          }
          plies++
        }

        val winner =
          if (simEngine.isFinished()) {
            simEngine.snapshot().winner ?: simEngine.currentScoreEstimate().winner
          } else {
            simEngine.currentScoreEstimate().winner
          }

        // 4. Backpropagation
        var cur: Node? = node
        while (cur != null) {
          cur.visits += 1
          if (cur.playerJustMoved == winner) {
            cur.wins += 1.0
          }
          cur = cur.parent
        }
      }

      val bestChild = root.children.maxByOrNull { it.visits }
      bestChild?.move ?: legalMoves.first()
    }
}
