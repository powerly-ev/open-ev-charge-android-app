package com.powerly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MyTriangle(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    height: Dp = 50.dp,
    base: Dp = 200.dp,
    flipped: Boolean = false
) {
    Canvas(
        modifier = Modifier
            .size(base, height)
            .then(modifier)
    ) {
        val x = size.width
        val y = size.height

        val trianglePath = if (flipped.not()) Path().apply {
            moveTo(x / 2, 0f)
            lineTo(x, y)
            lineTo(0f, y)
        } else Path().apply {
            moveTo(0f, 0f)
            lineTo(x, 0f)
            lineTo(x / 2, y)
        }

        drawPath(trianglePath, color = color)
    }
}



