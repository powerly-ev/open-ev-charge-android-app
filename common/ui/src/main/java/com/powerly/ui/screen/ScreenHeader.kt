package com.powerly.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.LayoutDirectionAny
import com.powerly.ui.theme.MyColors

@Composable
fun ScreenHeader(
    title: String,
    paddingHorizontal: Dp = 16.dp,
    paddingVertical: Dp = 16.dp,
    textAlign: TextAlign = TextAlign.Center,
    singleLine: Boolean = false,
    showDivider: Boolean = true,
    @DrawableRes closeIcon: Int? = R.drawable.close,
    @DrawableRes menuIcon: Int? = null,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    onClose: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = paddingHorizontal,
                        vertical = paddingVertical
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                closeIcon?.let {
                    Icon(
                        painter = painterResource(id = closeIcon),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable { onClose?.invoke() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                LayoutDirectionAny {
                    Text(
                        text = title,
                        textAlign = textAlign,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        overflow = if (singleLine) TextOverflow.Ellipsis
                        else TextOverflow.Clip,
                        maxLines = if (singleLine) 1 else Int.MAX_VALUE
                    )
                }
                menuIcon?.let {
                    Icon(
                        painter = painterResource(id = menuIcon),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable { onMenuClick?.invoke() }
                    )
                }
            }
            if (showDivider) HorizontalDivider(color = MyColors.dividerColor)
        }
    }
}