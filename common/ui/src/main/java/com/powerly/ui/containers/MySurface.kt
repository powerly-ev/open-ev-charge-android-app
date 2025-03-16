package com.powerly.ui.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MySurface(
    modifier: Modifier = Modifier,
    surfaceModifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    shape: CornerBasedShape = RoundedCornerShape(cornerRadius),
    color: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = color,
        modifier = surfaceModifier,
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
    ) {
        Box(
            modifier = modifier,
            content = content
        )
    }
}

@Composable
fun MySurfaceRow(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    border: BorderStroke? = null,
    color: Color = MaterialTheme.colorScheme.surface,
    spacing: Dp = 8.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        border = border,
        color = color,
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
        content = {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = verticalAlignment,
                content = content
            )
        }
    )
}

@Composable
fun MySurfaceColumn(
    modifier: Modifier = Modifier,
    surfaceModifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    border: BorderStroke? = null,
    color: Color = MaterialTheme.colorScheme.surface,
    spacing: Dp = 8.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = surfaceModifier,
        shape = RoundedCornerShape(cornerRadius),
        border = border,
        color = color,
        enabled = onClick != null,
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}
