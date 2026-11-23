package com.inscopelabs.amk.game.standardgo.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inscopelabs.amk.game.standardgo.controller.GameController
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.GameState
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.ui.board.BoardViewport
import com.inscopelabs.amk.game.standardgo.ui.board.GoBoard
import com.inscopelabs.amk.game.standardgo.ui.board.StoneBadge
import com.inscopelabs.amk.game.standardgo.util.formatGtpCoordinate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
  controller: GameController,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val state by controller.state.collectAsStateWithLifecycle()
  val config by controller.config.collectAsStateWithLifecycle()
  val isBotThinking by controller.isBotThinking.collectAsStateWithLifecycle()
  val pendingMove by controller.pendingMove.collectAsStateWithLifecycle()
  val statusMessage by controller.statusMessage.collectAsStateWithLifecycle()

  var confirmPlacement by remember(config.boardSize) { mutableStateOf(config.boardSize >= 19) }
  var showSgfDialog by remember { mutableStateOf(false) }
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
          Column {
            Text("Standard Go (${config.boardSize}×${config.boardSize})", style = MaterialTheme.typography.titleMedium)
            Text(
              text = "${config.mode.label} • Move ${state.moveCount}",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_to_menu_button")) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to menu")
          }
        },
        actions = {
          TextButton(onClick = { showSgfDialog = true }, modifier = Modifier.testTag("sgf_button")) {
            Text("SGF")
          }
          IconButton(
            onClick = { controller.newGame(config) },
            modifier = Modifier.testTag("new_game_button"),
          ) {
            Icon(Icons.Rounded.Refresh, contentDescription = "New game")
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
          .padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Column(
        modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        // Player info bar
        PlayerHudRow(
          state = state,
          isBotThinking = isBotThinking,
          isVsBot = config.mode == GameMode.HUMAN_VS_BOT,
        )

        // Tap-confirm toggle for dense boards
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Tap-tap confirmation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Switch(
            checked = confirmPlacement,
            onCheckedChange = {
              confirmPlacement = it
              if (!it) controller.cancelPendingMove()
            },
            modifier = Modifier.testTag("confirm_mode_switch"),
          )
        }

        // Main Board
        Box(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          contentAlignment = Alignment.Center,
        ) {
          BoardViewport(
            boardSize = config.boardSize,
            modifier = Modifier.fillMaxSize(),
          ) {
            GoBoard(
              state = state,
              pendingMove = pendingMove,
              interactive = !isBotThinking && !state.isGameOver,
              onIntersectionTap = { row, col ->
                controller.onBoardTapped(row, col, requireConfirm = confirmPlacement)
              },
            )
          }
        }

        // Pending confirmation bar if active
        if (pendingMove != null) {
          val (pr, pc) = pendingMove!!
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = "Selected ${formatGtpCoordinate(pr, pc, config.boardSize)}",
                style = MaterialTheme.typography.titleMedium,
              )
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = { controller.cancelPendingMove() },
                  modifier = Modifier.testTag("cancel_move_button"),
                ) {
                  Icon(Icons.Rounded.Close, contentDescription = null)
                  Spacer(modifier = Modifier.widthIn(min = 4.dp))
                  Text("Cancel")
                }
                Button(
                  onClick = { controller.confirmPendingMove() },
                  modifier = Modifier.testTag("confirm_move_button"),
                ) {
                  Icon(Icons.Rounded.Check, contentDescription = null)
                  Spacer(modifier = Modifier.widthIn(min = 4.dp))
                  Text("Play")
                }
              }
            }
          }
        }

        // Action Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          OutlinedButton(
            onClick = { controller.undo() },
            enabled = state.canUndo && !isBotThinking,
            modifier = Modifier.weight(1f).testTag("undo_button"),
          ) {
            Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = null)
            Spacer(modifier = Modifier.widthIn(min = 4.dp))
            Text("Undo")
          }
          OutlinedButton(
            onClick = { controller.pass() },
            enabled = !state.isGameOver && !isBotThinking,
            modifier = Modifier.weight(1f).testTag("pass_button"),
          ) {
            Icon(Icons.Rounded.SkipNext, contentDescription = null)
            Spacer(modifier = Modifier.widthIn(min = 4.dp))
            Text("Pass")
          }
          OutlinedButton(
            onClick = { controller.resign() },
            enabled = !state.isGameOver,
            modifier = Modifier.weight(1f).testTag("resign_button"),
          ) {
            Icon(Icons.Rounded.Flag, contentDescription = null)
            Spacer(modifier = Modifier.widthIn(min = 4.dp))
            Text("Resign")
          }
        }
      }
    }
  }

  if (state.isGameOver) {
    val score = state.scoreSummary
    AlertDialog(
      onDismissRequest = {},
      title = {
        Text(
          text = "${state.winner?.displayName ?: "Match"} Wins",
          style = MaterialTheme.typography.headlineMedium,
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          if (state.resignedBy != null) {
            Text("${state.resignedBy?.displayName} resigned.")
          } else if (score != null) {
            Text("Ruleset: ${score.ruleset.label}")
            Text("Black: ${score.blackScore} (Territory ${score.blackTerritory}, Captures ${score.blackCaptures})")
            Text("White: ${score.whiteScore} (Territory ${score.whiteTerritory}, Captures ${score.whiteCaptures}, Komi ${score.komi})")
            Text("Margin: ${score.margin} pts", style = MaterialTheme.typography.titleMedium)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { controller.newGame(config) },
          modifier = Modifier.testTag("play_again_button"),
        ) {
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

  if (showSgfDialog) {
    AlertDialog(
      onDismissRequest = { showSgfDialog = false },
      title = { Text("SGF Record") },
      text = {
        Text(
          text = controller.exportSgf(),
          style = MaterialTheme.typography.labelMedium,
        )
      },
      confirmButton = {
        TextButton(onClick = { showSgfDialog = false }) {
          Text("Close")
        }
      },
    )
  }
}

@Composable
private fun PlayerHudRow(
  state: GameState,
  isBotThinking: Boolean,
  isVsBot: Boolean,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    PlayerStatusCard(
      team = Team.BLACK,
      label = "Black",
      captures = state.blackCaptures,
      isActive = !state.isGameOver && state.currentTurn == Team.BLACK,
      isThinking = false,
      modifier = Modifier.weight(1f),
    )
    PlayerStatusCard(
      team = Team.WHITE,
      label = if (isVsBot) "White (AI)" else "White",
      captures = state.whiteCaptures,
      isActive = !state.isGameOver && state.currentTurn == Team.WHITE,
      isThinking = isBotThinking,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun PlayerStatusCard(
  team: Team,
  label: String,
  captures: Int,
  isActive: Boolean,
  isThinking: Boolean,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
          else MaterialTheme.colorScheme.surface,
      ),
    border =
      BorderStroke(
        width = if (isActive) 1.5.dp else 1.dp,
        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
      ),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StoneBadge(team = team, size = 22.dp)
        Column {
          Text(text = label, style = MaterialTheme.typography.titleMedium)
          Text(
            text = "Captures: $captures",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      if (isThinking) {
        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
      }
    }
  }
}
