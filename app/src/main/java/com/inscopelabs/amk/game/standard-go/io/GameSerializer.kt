package com.inscopelabs.amk.game.standardgo.io

import com.inscopelabs.amk.game.standardgo.engine.Move
import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.GameState
import com.inscopelabs.amk.game.standardgo.model.PlayerType
import com.inscopelabs.amk.game.standardgo.model.Ruleset
import com.inscopelabs.amk.game.standardgo.model.Team
import org.json.JSONArray
import org.json.JSONObject

object GameSerializer {
  fun serialize(config: GameConfig, state: GameState): String {
    val root = JSONObject()
    root.put(
      "config",
      JSONObject().apply {
        put("boardSize", config.boardSize)
        put("komi", config.komi)
        put("ruleset", config.ruleset.name)
        put("handicap", config.handicap)
        put("mode", config.mode.name)
        put("botType", config.botType.name)
        put("botSimulations", config.botSimulations)
      },
    )

    val gridArray = JSONArray()
    state.grid.forEach { gridArray.put(it) }

    root.put(
      "state",
      JSONObject().apply {
        put("boardSize", state.boardSize)
        put("grid", gridArray)
        put("currentTurn", state.currentTurn.name)
        put("blackCaptures", state.blackCaptures)
        put("whiteCaptures", state.whiteCaptures)
        put("consecutivePasses", state.consecutivePasses)
        put("moveCount", state.moveCount)
        put("positionHash", state.positionHash)
        put("isGameOver", state.isGameOver)
        state.resignedBy?.let { put("resignedBy", it.name) }
        state.winner?.let { put("winner", it.name) }
        state.koPoint?.let { (r, c) ->
          put("koRow", r)
          put("koCol", c)
        }
        state.lastMove?.let { m ->
          put(
            "lastMove",
            JSONObject().apply {
              put("row", m.row)
              put("col", m.col)
              put("isPass", m.isPass)
              put("isResign", m.isResign)
            },
          )
        }
      },
    )

    return root.toString()
  }

  fun deserialize(json: String): Pair<GameConfig, GameState>? =
    runCatching {
      val root = JSONObject(json)
      val cfgObj = root.getJSONObject("config")
      val config =
        GameConfig(
          boardSize = cfgObj.optInt("boardSize", 9),
          komi = cfgObj.optDouble("komi", 6.5),
          ruleset = Ruleset.valueOf(cfgObj.optString("ruleset", Ruleset.JAPANESE.name)),
          handicap = cfgObj.optInt("handicap", 0),
          mode = GameMode.valueOf(cfgObj.optString("mode", GameMode.HUMAN_VS_BOT.name)),
          botType = PlayerType.valueOf(cfgObj.optString("botType", PlayerType.MCTS_BOT.name)),
          botSimulations = cfgObj.optInt("botSimulations", 180),
        )

      val stObj = root.getJSONObject("state")
      val gridJson = stObj.getJSONArray("grid")
      val grid = List(gridJson.length()) { idx -> gridJson.getInt(idx) }

      val koPoint =
        if (stObj.has("koRow") && stObj.has("koCol")) {
          stObj.getInt("koRow") to stObj.getInt("koCol")
        } else {
          null
        }

      val lastMove =
        stObj.optJSONObject("lastMove")?.let { m ->
          Move(
            row = m.optInt("row", -1),
            col = m.optInt("col", -1),
            isPass = m.optBoolean("isPass", false),
            isResign = m.optBoolean("isResign", false),
          )
        }

      val state =
        GameState(
          boardSize = stObj.getInt("boardSize"),
          grid = grid,
          currentTurn = Team.valueOf(stObj.getString("currentTurn")),
          blackCaptures = stObj.getInt("blackCaptures"),
          whiteCaptures = stObj.getInt("whiteCaptures"),
          consecutivePasses = stObj.getInt("consecutivePasses"),
          moveCount = stObj.getInt("moveCount"),
          lastMove = lastMove,
          koPoint = koPoint,
          positionHash = stObj.optLong("positionHash", 0L),
          isGameOver = stObj.optBoolean("isGameOver", false),
          resignedBy = stObj.optString("resignedBy", "").takeIf { it.isNotEmpty() }?.let(Team::valueOf),
          winner = stObj.optString("winner", "").takeIf { it.isNotEmpty() }?.let(Team::valueOf),
        )

      config to state
    }.getOrNull()
}
