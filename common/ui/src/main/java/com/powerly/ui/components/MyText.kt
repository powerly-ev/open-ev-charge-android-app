package com.powerly.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun MyTextDynamic(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = LocalTextStyle.current,
) {
    var font by remember { mutableStateOf(style.fontSize) }
    Text(
        text = text,
        modifier = modifier,
        maxLines = 2,
        textAlign = textAlign,
        overflow = TextOverflow.Visible,
        style = style.copy(fontSize = font),
        color = color,
        onTextLayout = {
            if (it.lineCount == 2) font = (font.value - 1.sp.value).sp
        }
    )
}


@Composable
fun ItemsEmpty(
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
