package com.inscopelabs.amk.game.supergo.ui.board

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.inscopelabs.amk.game.supergo.model.Quadrant

@Composable
fun FogOverlay(
  revealedQuadrants: Set<Quadrant>,
  fogEnabled: Boolean,
  modifier: Modifier = Modifier,
) {
  if (!fogEnabled) return

  Column(modifier = modifier.fillMaxSize()) {
    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
      QuadrantMistCell(
        quadrant = Quadrant.NW,
        revealed = Quadrant.NW in revealedQuadrants,
        modifier = Modifier.weight(1f).fillMaxHeight(),
      )
      QuadrantMistCell(
        quadrant = Quadrant.NE,
        revealed = Quadrant.NE in revealedQuadrants,
        modifier = Modifier.weight(1f).fillMaxHeight(),
      )
    }
    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
      QuadrantMistCell(
        quadrant = Quadrant.SW,
        revealed = Quadrant.SW in revealedQuadrants,
        modifier = Modifier.weight(1f).fillMaxHeight(),
      )
      QuadrantMistCell(
        quadrant = Quadrant.SE,
        revealed = Quadrant.SE in revealedQuadrants,
        modifier = Modifier.weight(1f).fillMaxHeight(),
      )
    }
  }
}

@Composable
private fun QuadrantMistCell(
  quadrant: Quadrant,
  revealed: Boolean,
  modifier: Modifier = Modifier,
) {
  val alpha by animateFloatAsState(
    targetValue = if (revealed) 0f else 0.62f,
    animationSpec = tween(durationMillis = 320),
    label = "fog_alpha_${quadrant.name}",
  )

  Box(
    modifier = modifier.background(Color(0xFF1F2421).copy(alpha = alpha)),
    contentAlignment = Alignment.Center,
  ) {
    if (!revealed) {
      QuadrantToken(quadrant = quadrant)
    }
  }
}
