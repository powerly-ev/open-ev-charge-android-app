package com.powerly.ui.dialogs.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.powerly.core.model.util.Message
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.delay

@Preview
@Composable
fun MessageDialogPreview() {
    val messageState = rememberMessageState(
        initialMessage = Message("Hello", Message.ERROR)
    )
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            MessageDialog(messageState)
        }
    }
}

@Composable
fun MessageDialog(
    state: MessageState,
    timeout: Long = 2000L,
    onDone: () -> Unit = {}
) {
    state.message.value?.let { message ->
        LaunchedEffect(Unit) {
            delay(timeout)
            state.dismiss()
            onDone()
        }
        Popup(
            onDismissRequest = { },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                clippingEnabled = false
            ),
            offset = IntOffset(x = 0, y = -with(LocalDensity.current) { 36.dp.roundToPx() })
        ) {
            Surface(
                color = if (message.isError) MyColors.red500
                else MyColors.greenLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.msg,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 48.dp,
                            bottom = 24.dp
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDialog(
    message: Message,
    timeout: Long = 2000L,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(timeout)
        onDismiss()
    }
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
        modifier = Modifier.fillMaxSize(),
        shape = BottomSheetDefaults.HiddenShape,
        onDismissRequest = onDismiss,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        scrimColor = Color.Transparent,
        dragHandle = {},
    ) {
        Column(Modifier.fillMaxSize()) {
            Surface(
                color = if (message.isError) MyColors.red500
                else MyColors.greenLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.msg,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 48.dp,
                            bottom = 24.dp
                        )
                )
            }
        }
    }
}



