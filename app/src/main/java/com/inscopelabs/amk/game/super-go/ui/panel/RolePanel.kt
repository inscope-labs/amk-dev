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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.engine.RoleState
import com.inscopelabs.amk.game.supergo.model.Cycle

@Composable
fun RolePanel(
  roleState: RoleState,
  cycle: Cycle,
  currentTurn: Team,
  extraMovesRemaining: Int,
  canVeto: Boolean,
  onHandOffRole: () -> Unit,
  onRequestVeto: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val playerRole = roleState.statusFor(currentTurn)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Cycle ${cycle.cycleNumber} • ${currentTurn.displayName} (${playerRole.activeRole.title})",
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text =
            if (extraMovesRemaining > 0) "Extra moves remaining: $extraMovesRemaining"
            else playerRole.activeRole.summary,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(
          onClick = onHandOffRole,
          modifier = Modifier.testTag("handoff_role_button"),
        ) {
          Text("Hand Off")
        }
        OutlinedButton(
          onClick = onRequestVeto,
          enabled = canVeto,
          modifier = Modifier.testTag("veto_button"),
        ) {
          Text("Veto (-2)")
        }
      }
    }
  }
}
