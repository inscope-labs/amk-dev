package com.inscopelabs.amk.game.standardgo.ui.board

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.inscopelabs.amk.game.standardgo.model.GameState
import com.inscopelabs.amk.game.standardgo.model.Team
import com.inscopelabs.amk.game.standardgo.ui.theme.BoardRim
import com.inscopelabs.amk.game.standardgo.ui.theme.GridInk
import com.inscopelabs.amk.game.standardgo.ui.theme.KayaWood
import com.inscopelabs.amk.game.standardgo.ui.theme.KayaWoodDark
import com.inscopelabs.amk.game.standardgo.ui.theme.KayaWoodLight
import com.inscopelabs.amk.game.standardgo.util.gtpColumnLabels
import com.inscopelabs.amk.game.standardgo.util.hoshiPoints
import kotlin.math.roundToInt

@Composable
fun GoBoard(
  state: GameState,
  onIntersectionTap: (row: Int, col: Int) -> Unit,
  modifier: Modifier = Modifier,
  pendingMove: Pair<Int, Int>? = null,
  showCoordinates: Boolean = true,
  interactive: Boolean = true,
  overlayContent: (@Composable (boardSize: Int) -> Unit)? = null,
) {
  val boardSize = state.boardSize

  Box(
    modifier =
      modifier
        .aspectRatio(1f)
        .testTag("go_board_canvas")
  ) {
    Canvas(
      modifier =
        Modifier
          .fillMaxSize()
          .then(
            if (interactive) {
              Modifier.pointerInput(boardSize, interactive) {
                detectTapGestures { offset ->
                  val side = size.width.toFloat().coerceAtMost(size.height.toFloat())
                  val margin = side * if (showCoordinates) 0.082f else 0.05f
                  val gridSpan = side - margin * 2f
                  val cell = gridSpan / (boardSize - 1).coerceAtLeast(1)

                  val col = ((offset.x - margin) / cell).roundToInt()
                  val row = ((offset.y - margin) / cell).roundToInt()

                  if (row in 0 until boardSize && col in 0 until boardSize) {
                    val ix = margin + col * cell
                    val iy = margin + row * cell
                    val distSq = (offset.x - ix) * (offset.x - ix) + (offset.y - iy) * (offset.y - iy)
                    if (distSq <= (cell * 0.56f) * (cell * 0.56f)) {
                      onIntersectionTap(row, col)
                    }
                  }
                }
              }
            } else {
              Modifier
            }
          )
    ) {
      val side = size.minDimension
      val margin = side * if (showCoordinates) 0.082f else 0.05f
      val gridSpan = side - margin * 2f
      val cell = gridSpan / (boardSize - 1).coerceAtLeast(1)

      // Kaya wood surface
      drawRoundRect(
        brush = Brush.linearGradient(listOf(KayaWoodLight, KayaWood, KayaWoodDark)),
        topLeft = Offset.Zero,
        size = Size(side, side),
        cornerRadius = CornerRadius(side * 0.025f, side * 0.025f),
      )

      // Subtle inner wood bevel
      drawRoundRect(
        color = BoardRim.copy(alpha = 0.55f),
        topLeft = Offset(1.5f, 1.5f),
        size = Size(side - 3f, side - 3f),
        cornerRadius = CornerRadius(side * 0.025f, side * 0.025f),
        style = Stroke(width = 3f),
      )

      // Grid lines
      for (i in 0 until boardSize) {
        val pos = margin + i * cell
        val strokeW = if (i == 0 || i == boardSize - 1) 2.2f else 1.3f
        drawLine(
          color = GridInk.copy(alpha = 0.85f),
          start = Offset(margin, pos),
          end = Offset(margin + gridSpan, pos),
          strokeWidth = strokeW,
        )
        drawLine(
          color = GridInk.copy(alpha = 0.85f),
          start = Offset(pos, margin),
          end = Offset(pos, margin + gridSpan),
          strokeWidth = strokeW,
        )
      }

      // Hoshi star points
      val hoshiRadius = (cell * 0.11f).coerceIn(2.6f, 5.8f)
      for ((hr, hc) in hoshiPoints(boardSize)) {
        drawCircle(
          color = GridInk,
          radius = hoshiRadius,
          center = Offset(margin + hc * cell, margin + hr * cell),
        )
      }

      // Coordinate labels
      if (showCoordinates) {
        drawCoordinates(boardSize, margin, cell, side)
      }

      // Ko square marker
      state.koPoint?.let { (kr, kc) ->
        if (state.cellAt(kr, kc) == 0) {
          val cx = margin + kc * cell
          val cy = margin + kr * cell
          val boxSide = cell * 0.26f
          drawRect(
            color = Color(0xFFB83B3B).copy(alpha = 0.78f),
            topLeft = Offset(cx - boxSide / 2f, cy - boxSide / 2f),
            size = Size(boxSide, boxSide),
            style = Stroke(width = 2.2f),
          )
        }
      }

      // Placed stones
      val stoneRadius = cell * 0.455f
      val lastMove = state.lastMove
      for (r in 0 until boardSize) {
        for (c in 0 until boardSize) {
          val team = Team.fromValue(state.cellAt(r, c)) ?: continue
          val isLast = lastMove != null && lastMove.isPlacement && lastMove.row == r && lastMove.col == c
          drawGoStone(
            center = Offset(margin + c * cell, margin + r * cell),
            radius = stoneRadius,
            team = team,
            isLastMove = isLast,
          )
        }
      }

      // Ghost stone for tap-tap confirmation
      if (pendingMove != null) {
        val (pr, pc) = pendingMove
        if (pr in 0 until boardSize && pc in 0 until boardSize && state.cellAt(pr, pc) == 0) {
          drawGoStone(
            center = Offset(margin + pc * cell, margin + pr * cell),
            radius = stoneRadius,
            team = state.currentTurn,
            alpha = 0.55f,
          )
        }
      }
    }

    overlayContent?.invoke(boardSize)
  }
}

private fun DrawScope.drawCoordinates(
  boardSize: Int,
  margin: Float,
  cell: Float,
  side: Float,
) {
  val colLabels = gtpColumnLabels(boardSize)
  val textSizePx = (cell * 0.34f).coerceIn(14f, side * 0.028f)
  val paint =
    Paint().apply {
      isAntiAlias = true
      color = GridInk.copy(alpha = 0.72f).toArgb()
      textSize = textSizePx
      textAlign = Paint.Align.CENTER
      typeface = Typeface.MONOSPACE
    }

  val topY = margin * 0.55f
  val bottomY = side - margin * 0.32f
  val leftX = margin * 0.44f
  val rightX = side - margin * 0.44f

  drawContext.canvas.nativeCanvas.apply {
    for (i in 0 until boardSize) {
      val x = margin + i * cell
      drawText(colLabels[i], x, topY, paint)
      drawText(colLabels[i], x, bottomY, paint)

      val y = margin + i * cell + textSizePx * 0.35f
      val rowNum = (boardSize - i).toString()
      drawText(rowNum, leftX, y, paint)
      drawText(rowNum, rightX, y, paint)
    }
  }
}
