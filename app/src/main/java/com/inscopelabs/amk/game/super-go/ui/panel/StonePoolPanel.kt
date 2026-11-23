package com.inscopelabs.amk.game.supergo.ui.panel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.ui.board.StoneBadge
import com.inscopelabs.amk.game.supergo.engine.ResourcePool

@Composable
fun StonePoolPanel(
  pool: ResourcePool,
  currentTurn: Team,
  canSacrifice: Boolean,
  onRequestSacrifice: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PoolStat(
          team = Team.BLACK,
          stones = pool.blackStones,
          zeroStreak = pool.blackZeroRotations,
          active = currentTurn == Team.BLACK,
        )
        PoolStat(
          team = Team.WHITE,
          stones = pool.whiteStones,
          zeroStreak = pool.whiteZeroRotations,
          active = currentTurn == Team.WHITE,
        )
      }

      OutlinedButton(
        onClick = onRequestSacrifice,
        enabled = canSacrifice,
        modifier = Modifier.testTag("sacrifice_button"),
      ) {
        Text("Sacrifice (-10)")
      }
    }
  }
}

@Composable
private fun PoolStat(
  team: Team,
  stones: Int,
  zeroStreak: Int,
  active: Boolean,
) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    StoneBadge(team = team, size = 18.dp)
    Column {
      Text(
        text = "${team.displayName}: $stones",
        style = MaterialTheme.typography.titleMedium,
        color =
          if (stones <= 5) MaterialTheme.colorScheme.error
          else if (active) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.onSurface,
      )
      if (zeroStreak > 0) {
        Text(
          text = "Collapse $zeroStreak/3",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}
