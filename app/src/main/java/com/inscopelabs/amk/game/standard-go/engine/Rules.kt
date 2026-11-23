package com.inscopelabs.amk.game.standardgo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

sealed class MoveValidation {
  data class Valid(
    val outcome: PlacementOutcome,
    val nextHash: Long,
    val nextKoPoint: Pair<Int, Int>?,
  ) : MoveValidation()

  data class Invalid(val reason: String) : MoveValidation()
}

object Rules {
  fun validateMove(
    board: Board,
    row: Int,
    col: Int,
    team: Team,
    koPoint: Pair<Int, Int>?,
    boardHistory: Set<Long>,
  ): MoveValidation {
    if (!board.inBounds(row, col)) {
      return MoveValidation.Invalid("Point is outside the board.")
    }
    if (!board.isEmpty(row, col)) {
      return MoveValidation.Invalid("Intersection is already occupied.")
    }
    if (koPoint != null && koPoint.first == row && koPoint.second == col) {
      return MoveValidation.Invalid("Illegal Ko recapture.")
    }

    val outcome = board.tryPlaceStone(row, col, team)
      ?: return MoveValidation.Invalid("Cannot place stone there.")

    if (outcome.selfLiberties == 0 && outcome.capturedStones.isEmpty()) {
      return MoveValidation.Invalid("Suicide moves are illegal.")
    }

    val boardHash = Zobrist.hashBoardOnly(outcome.nextBoard)
    if (boardHash in boardHistory) {
      return MoveValidation.Invalid("Superko violation: board position already occurred.")
    }

    val nextKo =
      if (outcome.capturedStones.size == 1) {
        val ownGroup = Liberty.findGroup(outcome.nextBoard, row, col)
        if (ownGroup != null && ownGroup.stones.size == 1 && ownGroup.libertyCount == 1) {
          outcome.capturedStones.first()
        } else {
          null
        }
      } else {
        null
      }

    return MoveValidation.Valid(
      outcome = outcome,
      nextHash = boardHash,
      nextKoPoint = nextKo,
    )
  }
}
