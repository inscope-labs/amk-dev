package com.inscopelabs.amk.game.standardgo.ui.board

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomOutMap
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun BoardViewport(
  boardSize: Int,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  var scale by remember(boardSize) { mutableFloatStateOf(1f) }
  var offset by remember(boardSize) { mutableStateOf(Offset.Zero) }
  val zoomEnabled = boardSize >= 13

  BoxWithConstraints(
    modifier = modifier.clipToBounds(),
    contentAlignment = Alignment.Center,
  ) {
    val maxWidthPx = constraints.maxWidth.toFloat()
    val maxHeightPx = constraints.maxHeight.toFloat()

    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .then(
            if (zoomEnabled) {
              Modifier.pointerInput(boardSize) {
                detectTransformGestures { _, pan, zoom, _ ->
                  val newScale = (scale * zoom).coerceIn(1f, 2.8f)
                  val maxPanX = ((newScale - 1f) * maxWidthPx) / 2f
                  val maxPanY = ((newScale - 1f) * maxHeightPx) / 2f
                  scale = newScale
                  offset =
                    if (newScale <= 1.01f) {
                      Offset.Zero
                    } else {
                      Offset(
                        x = (offset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                        y = (offset.y + pan.y).coerceIn(-maxPanY, maxPanY),
                      )
                    }
                }
              }
            } else {
              Modifier
            }
          )
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offset.x
            translationY = offset.y
          },
      contentAlignment = Alignment.Center,
    ) {
      content()
    }

    if (zoomEnabled && scale > 1.05f) {
      FilledTonalIconButton(
        onClick = {
          scale = 1f
          offset = Offset.Zero
        },
        modifier =
          Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .size(48.dp)
            .testTag("reset_zoom_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.ZoomOutMap,
          contentDescription = "Reset board zoom",
        )
      }
    }
  }
}
