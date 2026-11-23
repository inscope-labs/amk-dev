package com.inscopelabs.amk.game.supergo.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SacrificeConfirmDialog(
  cost: Int,
  cooldownCycles: Int,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Confirm Cross-Board Sacrifice") },
    text = {
      Text(
        "Burn $cost stones from your shared pool to gain 1 immediate extra move across any board. " +
          "Sacrifice enters a $cooldownCycles-cycle cooldown after activation."
      )
    },
    confirmButton = {
      Button(onClick = onConfirm) {
        Text("Burn $cost Stones")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}
