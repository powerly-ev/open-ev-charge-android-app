package com.powerly.ui.dialogs.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
fun rememberLoadingState(visible: Boolean = false): LoadingState {
    return remember { LoadingState(visible) }
}


@Stable
class LoadingState(visible: Boolean = false) {
    internal var visibility = mutableStateOf(visible)
        private set

    fun show() {
        visibility.value = true
    }

    var show: Boolean
        get() = visibility.value
        set(value) {
            visibility.value = value
        }

    fun dismiss() {
        visibility.value = false
    }

    val isVisible: Boolean get() = visibility.value
}

