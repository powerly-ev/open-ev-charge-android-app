package com.powerly.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.dialogs.loading.LoadingDialog
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.message.MessageDialog
import com.powerly.ui.dialogs.success.SuccessDialog
import com.powerly.ui.extensions.thenIf


@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    verticalScroll: Boolean = false,
    background: Color = MaterialTheme.colorScheme.background,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    screenState: ScreenState? = null,
    header: @Composable () -> Unit = {},
    footer: @Composable BoxScope.() -> Unit = {},
    footerPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = header,
        bottomBar = { Box(Modifier.padding(footerPadding), content = footer) }
    ) { padding ->
        screenState?.successState?.let { SuccessDialog(state = it) }
        screenState?.messageState?.let { MessageDialog(state = it) }
        screenState?.loadingState?.let { LoadingDialog(state = it) }
        Box(
            modifier = Modifier
                .background(background)
                .padding(padding)
                .fillMaxSize()
        ) {
            MyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .thenIf(verticalScroll, Modifier.verticalScroll(rememberScrollState()))
                    .then(modifier),
                content = content,
                spacing = spacing,
                horizontalAlignment = horizontalAlignment
            )
        }
    }
}


