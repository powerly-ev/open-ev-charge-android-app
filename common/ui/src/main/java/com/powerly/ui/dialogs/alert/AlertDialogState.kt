package com.powerly.ui.dialogs.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
fun rememberAlertDialogState(
    visible: Boolean = false
): AlertDialogState {
    return remember {
        AlertDialogState(visible)
    }
}

fun initAlertDialogState(visible: Boolean = false) = AlertDialogState(visible)

@Stable
class AlertDialogState(visible: Boolean = false) {
    internal var visibility = mutableStateOf(visible)
        private set

    internal var message = mutableStateOf<String?>(null)
        private set

    internal var title = mutableStateOf<String?>(null)
        private set

    fun show() {
        visibility.value = true
    }

    fun show(message: String) {
        this.message.value = message
        visibility.value = true
    }

    fun show(title: String, message: String) {
        this.message.value = message
        visibility.value = true
    }

    fun dismiss() {
        visibility.value = false
    }

    val isVisible: Boolean get() = visibility.value
}

