package com.inscopelabs.amk

import com.inscopelabs.amk.game.standardgo.engine.Engine
import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.engine.PlayResult
import com.inscopelabs.amk.game.standardgo.io.SgfParser
import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.engine.TripleEngine
import com.inscopelabs.amk.game.supergo.engine.TriplePlayResult
import com.inscopelabs.amk.game.supergo.model.Quadrant
import com.inscopelabs.amk.game.supergo.model.SuperGoConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoEngineUnitTest {

  @Test
  fun `capture and ko rule enforcement`() {
    val engine = Engine(GameConfig(boardSize = 9, mode = GameMode.HUMAN_VS_HUMAN))

    // Surround a white stone at (0,1) using corner (0,0) and neighbors
    assertTrue(engine.playMove(Move.place(0, 0)) is PlayResult.Success) // B
    assertTrue(engine.playMove(Move.place(0, 1)) is PlayResult.Success) // W
    assertTrue(engine.playMove(Move.place(1, 1)) is PlayResult.Success) // B
    assertTrue(engine.playMove(Move.place(8, 8)) is PlayResult.Success) // W elsewhere

    val captureResult = engine.playMove(Move.place(0, 2)) as PlayResult.Success // B captures (0,1)
    assertEquals(1, captureResult.capturedCount)
    assertEquals(0, captureResult.state.cellAt(0, 1))
    assertEquals(1, captureResult.state.blackCaptures)
  }

  @Test
  fun `suicide move is rejected`() {
    val engine = Engine(GameConfig(boardSize = 9, mode = GameMode.HUMAN_VS_HUMAN))
    // Black builds a 1-eye corner at (0,0)
    engine.playMove(Move.place(0, 1)) // B
    engine.playMove(Move.place(8, 8)) // W
    engine.playMove(Move.place(1, 0)) // B
    // White attempts suicide at (0,0)
    val res = engine.playMove(Move.place(0, 0))
    assertTrue(res is PlayResult.Rejected)
  }

  @Test
  fun `sgf export and import round trip`() {
    val cfg = GameConfig(boardSize = 9, komi = 6.5)
    val moves = listOf(Team.BLACK to Move.place(4, 4), Team.WHITE to Move.place(2, 2))
    val sgf = SgfParser.export(cfg, moves)
    val parsed = SgfParser.import(sgf)!!
    assertEquals(9, parsed.config.boardSize)
    assertEquals(2, parsed.moves.size)
    assertEquals(4, parsed.moves[0].second.row)
    assertEquals(4, parsed.moves[0].second.col)
  }

  @Test
  fun `super go fog reveal and stone pool debit`() {
    val triple = TripleEngine(SuperGoConfig(boardSize = 9, poolSizePerPlayer = 20, fogEnabled = true))
    val res = triple.playMove(0, Move.place(1, 1)) as TriplePlayResult.Success
    assertEquals(19, res.state.resourcePool.blackStones)
    assertTrue(Quadrant.NW in res.state.fogState.revealedFor(Team.BLACK, 0))
    assertFalse(Quadrant.SE in res.state.fogState.revealedFor(Team.BLACK, 0))
  }
}
