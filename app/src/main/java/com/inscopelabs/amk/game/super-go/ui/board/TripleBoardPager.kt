package com.inscopelabs.amk.game.supergo.ui.board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.ui.board.BoardViewport
import com.inscopelabs.amk.game.standardgo.ui.board.GoBoard
import com.inscopelabs.amk.game.supergo.engine.TripleGameState
import kotlinx.coroutines.launch

@Composable
fun TripleBoardPager(
  state: TripleGameState,
  viewerTeam: Team,
  interactive: Boolean,
  onSelectBoard: (Int) -> Unit,
  onPlayAt: (boardIndex: Int, row: Int, col: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val pagerState = rememberPagerState(initialPage = state.activeBoardIndex) { 3 }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(state.activeBoardIndex) {
    if (pagerState.currentPage != state.activeBoardIndex) {
      pagerState.animateScrollToPage(state.activeBoardIndex)
    }
  }

  LaunchedEffect(pagerState.currentPage) {
    if (pagerState.currentPage != state.activeBoardIndex) {
      onSelectBoard(pagerState.currentPage)
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    // 3-board mini-strip overview
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      for (idx in 0..2) {
        val boardState = state.boards[idx]
        val revealedCount = state.fogState.revealedFor(viewerTeam, idx).size
        val selected = pagerState.currentPage == idx

        Card(
          shape = RoundedCornerShape(12.dp),
          colors =
            CardDefaults.cardColors(
              containerColor =
                if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                else MaterialTheme.colorScheme.surface,
            ),
          border =
            BorderStroke(
              width = if (selected) 1.5.dp else 1.dp,
              color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            ),
          modifier =
            Modifier
              .weight(1f)
              .testTag("board_strip_$idx")
              .clickable {
                onSelectBoard(idx)
                coroutineScope.launch { pagerState.animateScrollToPage(idx) }
              },
        ) {
          Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
              text = "Board ${idx + 1}",
              style = MaterialTheme.typography.titleMedium,
            )
            Text(
              text = "Moves: ${boardState.moveCount} • Fog ${revealedCount}/4",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }

    // HorizontalPager of the 3 live GoBoards
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.weight(1f).fillMaxWidth(),
    ) { page ->
      val boardState = state.boards[page]
      val revealed = state.fogState.revealedFor(viewerTeam, page)

      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        BoardViewport(
          boardSize = boardState.boardSize,
          modifier = Modifier.fillMaxSize(),
        ) {
          GoBoard(
            state = boardState,
            interactive = interactive && !boardState.isGameOver,
            onIntersectionTap = { row, col -> onPlayAt(page, row, col) },
            overlayContent = {
              FogOverlay(
                revealedQuadrants = revealed,
                fogEnabled = state.fogState.enabled,
              )
            },
          )
        }
      }
    }
  }
}
