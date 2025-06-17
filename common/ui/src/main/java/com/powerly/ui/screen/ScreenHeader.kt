package com.powerly.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import com.powerly.resources.R
import com.powerly.ui.containers.LayoutDirectionAny
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
fun ScreenHeaderPreview() {
    AppTheme {
        ScreenHeader(
            title = "Screen Title",
            textAlign = TextAlign.Center,
            singleLine = false,
            showDivider = true,
            closeIcon = R.drawable.close,
            menuIcon = R.drawable.ic_add,
            layoutDirection = LayoutDirection.Ltr,
            onClose = {},
            onMenuClick = {}
        )
    }
}

@Preview
@Composable
fun DialogHeaderPreview() {
    AppTheme {
        DialogHeader(
            title = "Dialog Title",
            textAlign = TextAlign.Center,
            singleLine = false,
            showDivider = true,
            closeIcon = R.drawable.close,
            menuIcon = R.drawable.ic_add,
            layoutDirection = LayoutDirection.Ltr,
            onClose = {},
            onMenuClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHeader(
    title: String,
    textAlign: TextAlign = TextAlign.Center,
    singleLine: Boolean = false,
    showDivider: Boolean = true,
    @DrawableRes closeIcon: Int? = R.drawable.close,
    @DrawableRes menuIcon: Int? = null,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    backgroundColor: Color = Color.White,
    onClose: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Column(modifier = Modifier.background(color = backgroundColor)) {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                expandedHeight = 50.dp,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    LayoutDirectionAny {
                        Text(
                            text = title,
                            textAlign = textAlign,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            overflow = if (singleLine) TextOverflow.Ellipsis
                            else TextOverflow.Clip,
                            maxLines = if (singleLine) 1 else Int.MAX_VALUE
                        )
                    }
                },
                navigationIcon = {
                    closeIcon?.let {
                        IconButton(
                            onClick = { onClose?.invoke() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(it),
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = ""
                            )
                        }
                    }
                },
                actions = {
                    menuIcon?.let {
                        IconButton(
                            onClick = { onMenuClick?.invoke() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(it),
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = ""
                            )

                        }
                    }
                }
            )
            if (showDivider) HorizontalDivider(color = MyColors.dividerColor)
        }
    }
}

@Composable
fun DialogHeader(
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
