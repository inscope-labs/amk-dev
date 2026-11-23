package com.inscopelabs.amk.game.supergo.ui.board

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.inscopelabs.amk.game.supergo.model.Quadrant

@Composable
fun QuadrantToken(
  quadrant: Quadrant,
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition(label = "quadrant_pulse")
  val pulseAlpha by transition.animateFloat(
    initialValue = 0.45f,
    targetValue = 0.95f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(durationMillis = 1200),
        repeatMode = RepeatMode.Reverse,
      ),
    label = "quadrant_pulse_alpha",
  )

  Box(
    modifier =
      modifier
        .alpha(pulseAlpha)
        .background(
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
          shape = RoundedCornerShape(999.dp),
        )
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
          shape = RoundedCornerShape(999.dp),
        )
        .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "${quadrant.label} Fog",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
