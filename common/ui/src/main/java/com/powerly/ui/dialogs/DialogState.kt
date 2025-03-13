package com.powerly.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberMyDialogState(visible: Boolean = false): MyDialogState {
    return remember { MyDialogState(visible) }
}

fun initMyDialogState(visible: Boolean = false) = MyDialogState(visible)

@Stable
class MyDialogState(visible: Boolean = false) {
    internal var visibility = mutableStateOf(visible)
        private set

    fun show() {
        visibility.value = true
    }

    var visible: Boolean
        get() = visibility.value
        set(value) {
            visibility.value = value
        }

    fun dismiss() {
        visibility.value = false
    }

    val isVisible: Boolean get() = visibility.value
}

