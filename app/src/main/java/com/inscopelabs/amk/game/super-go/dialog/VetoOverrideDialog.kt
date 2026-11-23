package com.inscopelabs.amk.game.supergo.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun VetoOverrideDialog(
  cost: Int,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Confirm Veto Override") },
    text = {
      Text(
        "Spend $cost stones from your shared pool to force an immediate Role hand-off " +
          "and reveal all Fog of War quadrants on the currently selected board."
      )
    },
    confirmButton = {
      Button(onClick = onConfirm) {
        Text("Spend $cost Stones")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}
