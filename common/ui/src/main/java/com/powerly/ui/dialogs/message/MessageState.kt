package com.powerly.ui.dialogs.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.powerly.core.model.util.Message

@Composable
fun rememberMessageState(
    initialMessage: Message? = null
): MessageState {
    return remember {
        MessageState(initialMessage)
    }
}

@Stable
class MessageState(initialMessage: Message? = null) {
    internal var message = mutableStateOf<Message?>(initialMessage)
        private set

    private var onDismiss: (() -> Unit)? = null

    fun show(message: Message) {
        this.message.value = message
    }

    fun show(message: Message, onDone: () -> Unit) {
        onDismiss = onDone
        this.message.value = message
    }

    fun dismiss() {
        this.message.value = null
        onDismiss?.invoke()
        onDismiss = null
    }
}
