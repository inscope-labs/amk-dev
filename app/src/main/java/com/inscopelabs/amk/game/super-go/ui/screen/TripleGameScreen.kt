package com.inscopelabs.amk.game.supergo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.supergo.controller.TripleGameController
import com.inscopelabs.amk.game.supergo.dialog.SacrificeConfirmDialog
import com.inscopelabs.amk.game.supergo.dialog.VetoOverrideDialog
import com.inscopelabs.amk.game.supergo.ui.board.TripleBoardPager
import com.inscopelabs.amk.game.supergo.ui.panel.RolePanel
import com.inscopelabs.amk.game.supergo.ui.panel.StonePoolPanel
import com.inscopelabs.amk.game.supergo.ui.panel.SynergyTrackerPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripleGameScreen(
  controller: TripleGameController,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val state by controller.state.collectAsStateWithLifecycle()
  val config by controller.config.collectAsStateWithLifecycle()
  val isBotThinking by controller.isBotThinking.collectAsStateWithLifecycle()
  val statusMessage by controller.statusMessage.collectAsStateWithLifecycle()
  val showSacrificeDialog by controller.showSacrificeDialog.collectAsStateWithLifecycle()
  val showVetoDialog by controller.showVetoDialog.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(statusMessage) {
    val msg = statusMessage
    if (!msg.isNullOrBlank()) {
      snackbarHostState.showSnackbar(msg)
      controller.clearStatusMessage()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column {
              Text("Super Go (${config.boardSize}×${config.boardSize} × 3)", style = MaterialTheme.typography.titleMedium)
              Text(
                text = "Turn: ${state.currentTurn.displayName} • Cycle ${state.cycle.cycleNumber}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            if (isBotThinking) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("triple_back_button")) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to menu")
          }
        },
        actions = {
          IconButton(
            onClick = { controller.newGame(config) },
            modifier = Modifier.testTag("triple_new_game_button"),
          ) {
            Icon(Icons.Rounded.Refresh, contentDescription = "Restart Super Go")
          }
        },
      )
    },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier.widthIn(max = 680.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        StonePoolPanel(
          pool = state.resourcePool,
          currentTurn = state.currentTurn,
          canSacrifice = !state.isGameOver && !isBotThinking && state.resourcePool.canAfford(state.currentTurn, config.sacrificeCost),
          onRequestSacrifice = { controller.requestSacrificeDialog() },
        )

        RolePanel(
          roleState = state.roleState,
          cycle = state.cycle,
          currentTurn = state.currentTurn,
          extraMovesRemaining = state.extraMovesRemaining,
          canVeto = !state.isGameOver && !isBotThinking && state.resourcePool.canAfford(state.currentTurn, config.vetoCost),
          onHandOffRole = { controller.handOffRole() },
          onRequestVeto = { controller.requestVetoDialog() },
        )

        TripleBoardPager(
          state = state,
          viewerTeam = if (isBotThinking) Team.BLACK else state.currentTurn,
          interactive = !isBotThinking && !state.isGameOver,
          onSelectBoard = { controller.selectBoard(it) },
          onPlayAt = { boardIdx, row, col -> controller.playAt(boardIdx, row, col) },
          modifier = Modifier.weight(1f),
        )

        SynergyTrackerPanel(
          tracker = state.synergyTracker,
          currentTurn = state.currentTurn,
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          OutlinedButton(
            onClick = { controller.pass() },
            enabled = !state.isGameOver && !isBotThinking,
            modifier = Modifier.weight(1f).testTag("triple_pass_button"),
          ) {
            Icon(Icons.Rounded.SkipNext, contentDescription = null)
            Spacer(modifier = Modifier.widthIn(min = 4.dp))
            Text("Pass Turn")
          }
          OutlinedButton(
            onClick = { controller.resign() },
            enabled = !state.isGameOver,
            modifier = Modifier.weight(1f).testTag("triple_resign_button"),
          ) {
            Icon(Icons.Rounded.Flag, contentDescription = null)
            Spacer(modifier = Modifier.widthIn(min = 4.dp))
            Text("Resign")
          }
        }
      }
    }
  }

  if (showSacrificeDialog) {
    SacrificeConfirmDialog(
      cost = config.sacrificeCost,
      cooldownCycles = config.sacrificeCooldownCycles,
      onConfirm = { controller.confirmSacrifice() },
      onDismiss = { controller.dismissSacrificeDialog() },
    )
  }

  if (showVetoDialog) {
    VetoOverrideDialog(
      cost = config.vetoCost,
      onConfirm = { controller.confirmVetoOverride() },
      onDismiss = { controller.dismissVetoDialog() },
    )
  }

  if (state.isGameOver) {
    AlertDialog(
      onDismissRequest = {},
      title = { Text("${state.winner?.displayName ?: "Match"} Wins Super Go") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(state.gameOverReason ?: "Match finished.")
          state.boardScores.forEachIndexed { idx, score ->
            Text("Board ${idx + 1}: ${score.winner.displayName} (${score.blackScore} vs ${score.whiteScore})")
          }
        }
      },
      confirmButton = {
        Button(onClick = { controller.newGame(config) }) {
          Text("Play Again")
        }
      },
      dismissButton = {
        TextButton(onClick = onNavigateBack) {
          Text("Menu")
        }
      },
    )
  }
}
