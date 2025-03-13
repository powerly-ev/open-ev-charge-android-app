package com.powerly.ui.dialogs.success

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
fun rememberSuccessState(visible: Boolean = false): SuccessState {
    return remember { SuccessState(visible) }
}


@Stable
class SuccessState(visible: Boolean = false) {
    internal var visibility = mutableStateOf(visible)
        private set

    private var onDismiss: (() -> Unit)? = null

    fun show() {
        visibility.value = true
    }

    fun show(onDone: () -> Unit) {
        onDismiss = onDone
        visibility.value = true
    }
        fun dismiss() {
        visibility.value = false
        onDismiss?.invoke()
        onDismiss = null
    }

    val isVisible: Boolean get() = visibility.value
}

