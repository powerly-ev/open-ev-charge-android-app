package com.powerly.ui.dialogs.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.powerly.ui.theme.AppTheme


@Preview
@Composable
fun LoadingScreenPreview() {
    val state = rememberLoadingState(visible = true)
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LoadingDialog(state = state)
        }
    }
}

private val dialogSize = 50.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    state: LoadingState = rememberLoadingState(),
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false
) {
    if (state.isVisible) {
        AppTheme {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = dismissOnBackPress,
                    dismissOnClickOutside = dismissOnClickOutside,
                    usePlatformDefaultWidth = false
                )
            ) {
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(dialogSize),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
    }
}

