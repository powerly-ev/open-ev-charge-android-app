package com.powerly.ui.dialogs.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.powerly.core.model.util.Message
import com.powerly.ui.dialogs.message.MessageState
import com.powerly.ui.dialogs.success.SuccessState


@Composable
fun rememberScreenState(
    loadingState: LoadingState? = LoadingState(),
    messageState: MessageState? = MessageState(),
    successState: SuccessState? = SuccessState()
): ScreenState {
    return remember {
        ScreenState(loadingState, messageState, successState)
    }
}

@Composable
fun rememberBasicScreenState(
    loadingState: LoadingState? = null,
    messageState: MessageState? = null,
    successState: SuccessState? = null
): ScreenState {
    return remember {
        ScreenState(loadingState, messageState, successState)
    }
}

fun initScreenState() = ScreenState(LoadingState(), MessageState(), SuccessState())
fun initBasicScreenState(
    loadingState: LoadingState?,
    messageState: MessageState? = null,
    successState: SuccessState? = null
) = ScreenState(loadingState, messageState, successState)


@Stable
class ScreenState(
    val loadingState: LoadingState?,
    val messageState: MessageState?,
    val successState: SuccessState?
) {
    fun showSuccess() {
        successState?.show()
    }

    fun showSuccess(onDone: () -> Unit) {
        successState?.show(onDone)
    }

    fun showMessage(message: Message) {
        messageState?.show(message)
    }

    fun showMessage(message: Message, onDone: () -> Unit) {
        messageState?.show(message, onDone)
    }

    fun showMessage(text: String) {
        messageState?.show(Message(text))
    }

    var loading: Boolean
        get() = loadingState?.isVisible == true
        set(value) {
            if (value) loadingState?.show()
            else loadingState?.dismiss()
        }
}

