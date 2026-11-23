package com.inscopelabs.amk.game.standardgo.controller

import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.engine.ScoreResult
import com.inscopelabs.amk.game.standardgo.model.Team

sealed class GameEvent {
  data class MoveMade(val team: Team, val move: Move) : GameEvent()
  data class Capture(val team: Team, val capturedCount: Int) : GameEvent()
  data class Pass(val team: Team) : GameEvent()
  data class GameOver(val winner: Team?, val score: ScoreResult?, val resignedBy: Team?) : GameEvent()
  data class IllegalMove(val reason: String) : GameEvent()
}
