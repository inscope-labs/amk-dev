package com.inscopelabs.amk.game.supergo.engine

import com.inscopelabs.amk.game.standardgo.model.Team

data class PlayerRoleStatus(
  val activeRole: Role = Role.OPENER,
  val movesInRole: Int = 0,
  val handoffsCompleted: Int = 0,
)

data class RoleState(
  val black: PlayerRoleStatus = PlayerRoleStatus(),
  val white: PlayerRoleStatus = PlayerRoleStatus(),
) {
  fun statusFor(team: Team): PlayerRoleStatus = if (team == Team.BLACK) black else white

  fun recordMove(team: Team): RoleState =
    if (team == Team.BLACK) {
      copy(black = black.copy(movesInRole = black.movesInRole + 1))
    } else {
      copy(white = white.copy(movesInRole = white.movesInRole + 1))
    }

  fun handOff(team: Team): RoleState =
    if (team == Team.BLACK) {
      copy(
        black =
          PlayerRoleStatus(
            activeRole = black.activeRole.next(),
            movesInRole = 0,
            handoffsCompleted = black.handoffsCompleted + 1,
          )
      )
    } else {
      copy(
        white =
          PlayerRoleStatus(
            activeRole = white.activeRole.next(),
            movesInRole = 0,
            handoffsCompleted = white.handoffsCompleted + 1,
          )
      )
    }
}
