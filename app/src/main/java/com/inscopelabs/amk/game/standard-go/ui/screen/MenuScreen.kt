package com.inscopelabs.amk.game.standardgo.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.standardgo.model.GameConfig
import com.inscopelabs.amk.game.standardgo.model.GameMode
import com.inscopelabs.amk.game.standardgo.model.Ruleset
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.ui.board.StoneBadge
import com.inscopelabs.amk.game.supergo.engine.SunTzuConfig
import com.inscopelabs.amk.game.supergo.model.SuperGoConfig

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuScreen(
  hasSavedStandardGame: Boolean,
  onStartStandardGo: (GameConfig) -> Unit,
  onResumeStandardGo: () -> Unit,
  onStartSuperGo: (SuperGoConfig) -> Unit,
  modifier: Modifier = Modifier,
) {
  var boardSize by remember { mutableIntStateOf(9) }
  var mode by remember { mutableStateOf(GameMode.HUMAN_VS_BOT) }
  var ruleset by remember { mutableStateOf(Ruleset.JAPANESE) }
  var fogEnabled by remember { mutableStateOf(true) }
  var poolSize by remember { mutableIntStateOf(60) }
  var sunTzuFeint by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          StoneBadge(team = Team.BLACK, size = 26.dp)
          StoneBadge(team = Team.WHITE, size = 26.dp)
          Text(
            text = "AMK Go Studio",
            style = MaterialTheme.typography.headlineLarge,
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Choose Standard Go or Super Go triple-board strategy.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Shared configuration card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(text = "Board & Match Setup", style = MaterialTheme.typography.titleMedium)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Board Size",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(9, 13, 19).forEach { sizeOption ->
                  FilterChip(
                    selected = boardSize == sizeOption,
                    onClick = { boardSize = sizeOption },
                    label = { Text("${sizeOption}×${sizeOption}") },
                    modifier = Modifier.testTag("board_size_$sizeOption"),
                  )
                }
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Opponent",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GameMode.entries.forEach { m ->
                  FilterChip(
                    selected = mode == m,
                    onClick = { mode = m },
                    label = { Text(m.label) },
                    modifier = Modifier.testTag("mode_${m.name.lowercase()}"),
                  )
                }
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Ruleset",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Ruleset.entries.forEach { r ->
                  FilterChip(
                    selected = ruleset == r,
                    onClick = { ruleset = r },
                    label = { Text(r.label) },
                    modifier = Modifier.testTag("ruleset_${r.name.lowercase()}"),
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Standard Go Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth().testTag("standard_go_card"),
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(Icons.Rounded.GridOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text(text = "Standard Go", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
              text = "Classic single-board Go with Ko & positional Superko, MCTS AI, tap-confirm on 19×19, and SGF export.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = {
                  onStartStandardGo(
                    GameConfig(
                      boardSize = boardSize,
                      ruleset = ruleset,
                      mode = mode,
                    )
                  )
                },
                modifier = Modifier.weight(1f).testTag("start_standard_go_button"),
              ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.widthIn(min = 6.dp))
                Text("Play Standard Go")
              }
              if (hasSavedStandardGame) {
                OutlinedButton(
                  onClick = onResumeStandardGo,
                  modifier = Modifier.testTag("resume_standard_go_button"),
                ) {
                  Icon(Icons.Rounded.Restore, contentDescription = null)
                  Spacer(modifier = Modifier.widthIn(min = 4.dp))
                  Text("Resume")
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Super Go Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth().testTag("super_go_card"),
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text(text = "Super Go (Triple-Board)", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
              text = "Command 3 linked boards simultaneously with a shared stone pool, Fog of War quadrants, Role hand-offs, Cross-Board Sacrifice, and Triple Synergy bonuses.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Fog of War Quadrants", style = MaterialTheme.typography.titleMedium)
                Text(
                  "Quadrants stay veiled until a stone reveals them",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Switch(
                checked = fogEnabled,
                onCheckedChange = { fogEnabled = it },
                modifier = Modifier.testTag("fog_toggle"),
              )
            }

            if (mode == GameMode.HUMAN_VS_BOT) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text("Sun Tzu AI Feint", style = MaterialTheme.typography.titleMedium)
                  Text(
                    "AI periodically probes off-center distraction boards",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                Switch(
                  checked = sunTzuFeint,
                  onCheckedChange = { sunTzuFeint = it },
                  modifier = Modifier.testTag("suntzu_toggle"),
                )
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "Shared Stone Pool",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(40, 60, 90).forEach { poolOption ->
                  FilterChip(
                    selected = poolSize == poolOption,
                    onClick = { poolSize = poolOption },
                    label = { Text("$poolOption stones") },
                    modifier = Modifier.testTag("pool_size_$poolOption"),
                  )
                }
              }
            }

            Button(
              onClick = {
                onStartSuperGo(
                  SuperGoConfig(
                    boardSize = boardSize,
                    ruleset = ruleset,
                    mode = mode,
                    poolSizePerPlayer = poolSize,
                    fogEnabled = fogEnabled,
                    sunTzu = SunTzuConfig(feint = sunTzuFeint),
                  )
                )
              },
              modifier = Modifier.fillMaxWidth().testTag("start_super_go_button"),
            ) {
              Icon(Icons.Rounded.PlayArrow, contentDescription = null)
              Spacer(modifier = Modifier.widthIn(min = 6.dp))
              Text("Launch Super Go")
            }
          }
        }
      }
    }
  }
}
