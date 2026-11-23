package com.inscopelabs.amk.game.standardgo.ui.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.ui.theme.StoneBlackCore
import com.inscopelabs.amk.game.standardgo.ui.theme.StoneBlackHighlight
import com.inscopelabs.amk.game.standardgo.ui.theme.StoneWhiteCore
import com.inscopelabs.amk.game.standardgo.ui.theme.StoneWhiteShade

fun DrawScope.drawGoStone(
  center: Offset,
  radius: Float,
  team: Team,
  alpha: Float = 1f,
  isLastMove: Boolean = false,
) {
  // Subtle drop shadow
  drawCircle(
    color = Color.Black.copy(alpha = 0.28f * alpha),
    radius = radius * 1.02f,
    center = Offset(center.x + radius * 0.08f, center.y + radius * 0.12f),
  )

  val highlightCenter = Offset(center.x - radius * 0.30f, center.y - radius * 0.32f)
  val brush =
    when (team) {
      Team.BLACK ->
        Brush.radialGradient(
          colors = listOf(StoneBlackHighlight.copy(alpha = alpha), StoneBlackCore.copy(alpha = alpha)),
          center = highlightCenter,
          radius = radius * 1.4f,
        )
      Team.WHITE ->
        Brush.radialGradient(
          colors = listOf(StoneWhiteCore.copy(alpha = alpha), StoneWhiteShade.copy(alpha = alpha)),
          center = highlightCenter,
          radius = radius * 1.55f,
        )
    }

  drawCircle(
    brush = brush,
    radius = radius,
    center = center,
  )

  if (team == Team.WHITE) {
    drawCircle(
      color = Color.Black.copy(alpha = 0.16f * alpha),
      radius = radius,
      center = center,
      style = Stroke(width = (radius * 0.07f).coerceAtLeast(1f)),
    )
  }

  if (isLastMove) {
    val ringColor = if (team == Team.BLACK) Color.White.copy(alpha = 0.85f) else Color(0xFF1F2937).copy(alpha = 0.85f)
    drawCircle(
      color = ringColor,
      radius = radius * 0.36f,
      center = center,
      style = Stroke(width = (radius * 0.14f).coerceAtLeast(1.8f)),
    )
  }
}

@Composable
fun StoneBadge(
  team: Team,
  modifier: Modifier = Modifier,
  size: Dp = 20.dp,
) {
  Canvas(modifier = modifier.size(size)) {
    drawGoStone(
      center = Offset(this.size.width / 2f, this.size.height / 2f),
      radius = this.size.minDimension * 0.44f,
      team = team,
    )
  }
}
