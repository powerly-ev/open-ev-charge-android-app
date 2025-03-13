package com.powerly.ui.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.ui.extensions.thenIf


@Composable
fun MyCardRow(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    background: Color = Color.White,
    padding: PaddingValues = PaddingValues(0.dp),
    margin: PaddingValues = PaddingValues(0.dp),
    spacing: Dp = 0.dp,
    borderStroke: BorderStroke? = null,
    elevation: CardElevation = CardDefaults.cardElevation(),
    fillMaxWidth: Boolean = true,
    fillMaxHeight: Boolean = false,
    horizontalScroll: Boolean = false,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {

    val cardModifier = modifier
        .then(Modifier.background(Color.Transparent))
        .then(Modifier.padding(margin))
        .then(
            if (fillMaxHeight) Modifier.fillMaxHeight()
            else Modifier.height(IntrinsicSize.Min)
        )
        .then(
            if (fillMaxWidth) Modifier.fillMaxWidth()
            else Modifier.width(IntrinsicSize.Min)
        )
        .thenIf(
            onClick != null,
            Modifier.clickable { onClick?.invoke() }
        )

    val innerModifier = Modifier
        .background(background)
        .padding(padding)
        .fillMaxHeight()
        .fillMaxWidth()
        .thenIf(horizontalScroll, Modifier.horizontalScroll(rememberScrollState()))

    Card(
        shape = RoundedCornerShape(cornerRadius),
        border = borderStroke,
        elevation = elevation,
        modifier = cardModifier,
    ) {
        Row(
            modifier = innerModifier,
            verticalAlignment = verticalAlignment,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            content = content
        )
    }
}


@Composable
fun MyCardColum(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    background: Color = Color.White,
    padding: PaddingValues = PaddingValues(0.dp),
    spacing: Dp = 0.dp,
    borderStroke: BorderStroke? = null,
    fillMaxWidth: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalScroll: Boolean = false,
    fillMaxHeight: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {

    val cardModifier = modifier
        .then(Modifier.background(Color.Transparent))
        .then(
            if (fillMaxHeight) Modifier.fillMaxHeight()
            else Modifier.height(IntrinsicSize.Min)
        )
        .then(
            if (fillMaxWidth) Modifier.fillMaxWidth()
            else Modifier.width(IntrinsicSize.Min)
        )
        .thenIf(
            onClick != null,
            Modifier.clickable { onClick?.invoke() }
        )


    val innerModifier = Modifier
        .background(background)
        .padding(padding)
        .fillMaxHeight()
        .fillMaxWidth()
        .thenIf(verticalScroll, Modifier.verticalScroll(rememberScrollState()))

    Card(
        shape = RoundedCornerShape(cornerRadius),
        border = borderStroke,
        modifier = cardModifier,
    ) {
        Column(
            modifier = innerModifier,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content
        )
    }
}