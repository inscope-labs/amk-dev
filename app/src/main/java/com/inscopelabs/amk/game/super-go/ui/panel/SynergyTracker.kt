package com.inscopelabs.amk.game.supergo.ui.panel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.engine.SynergyTracker

@Composable
fun SynergyTrackerPanel(
  tracker: SynergyTracker,
  currentTurn: Team,
  modifier: Modifier = Modifier,
) {
  val capturedBoards = tracker.boardsCapturedFor(currentTurn)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Triple Synergy (${capturedBoards.size}/3 boards)",
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text = "B:${tracker.blackTripleCount}  W:${tracker.whiteTripleCount}",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (idx in 0..2) {
          val lit = idx in capturedBoards
          AssistChip(
            onClick = {},
            label = {
              Text(
                text = if (lit) "B${idx + 1} ✓" else "B${idx + 1}",
                style = MaterialTheme.typography.labelMedium,
              )
            },
          )
        }
      }

      tracker.recentLog.firstOrNull()?.let { latest ->
        Text(
          text = latest,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
