package com.powerly.ui.dialogs.alert

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.LayoutDirection
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.powerly.ui.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Data class to hold additional properties for PowerlyDialog.
 */
data class AlertDialogProperties(
    val type: Int = ALERT_DIALOG,
    val messageAlign: TextAlign = TextAlign.Center,
    val messageJustify: Boolean = false,
    val messageRTL: Int = LayoutDirection.LOCALE,
    val subMessageAlign: TextAlign = TextAlign.Center,
    val subMessageRTL: Int = LayoutDirection.LOCALE,
    val canDrag: Boolean = false,
    val canDismiss: Boolean = true,
    val showClose: Boolean = true,
    val showDrag: Boolean = false,
) {
    companion object {
        const val ALERT_DIALOG = 0
        const val MODAL_DIALOG = 1
    }
}

/**
 * Customizable dialog composable using Jetpack Compose, replacing the builder pattern.
 *
 * This composable provides a flexible way to create different types of dialogs,
 * including alert dialogs and bottom sheet dialogs.
 *
 * @param title Title of the dialog.
 * @param message Main message of the dialog.
 * @param spannedMessage Spanned version of the main message.
 * @param subMessage Secondary message for the dialog.
 * @param spannedSubMessage Spanned version of the secondary message.
 * @param icon Icon to be displayed in the dialog.
 * @param positiveButton Text of the positive button.
 * @param positiveButtonClick Callback to be invoked when the positive button is clicked.
 * @param negativeButton Text of the negative button.
 * @param negativeButtonClick Callback to be invoked when the negative button is clicked.
 * @param closeButtonClick Callback to be invoked when the close button is clicked.
 * @param onDismiss Callback to be invoked when the dialog is dismissed.
 * @param properties Additional properties for the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAlertDialog(
    title: String? = null,
    message: String? = null,
    spannedMessage: Spanned? = null,
    subMessage: String? = null,
    spannedSubMessage: Spanned? = null,
    icon: Int? = null,
    positiveButton: String? = null,
    positiveButtonClick: (() -> Unit)? = null,
    negativeButton: String? = null,
    negativeButtonClick: (() -> Unit)? = null,
    closeButtonClick: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
    state: AlertDialogState = rememberAlertDialogState(),
    properties: AlertDialogProperties = AlertDialogProperties(),
) {
    val hideDialog = {
        state.dismiss()
        onDismiss()
    }

    if (state.isVisible) when (properties.type) {
        AlertDialogProperties.MODAL_DIALOG -> {
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            )
            val sheetProperties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = properties.canDismiss,
            )

            val scope = rememberCoroutineScope()

            ModalBottomSheet(
                onDismissRequest = {
                    hideDialog()
                    closeButtonClick?.invoke()
                },
                sheetState = sheetState,
                properties = sheetProperties,
                shape = BottomSheetDefaults.HiddenShape,
                containerColor = Color.Transparent,
                dragHandle = { if (properties.showDrag) BottomSheetDefaults.DragHandle() },
                scrimColor = BottomSheetDefaults.ScrimColor
            ) {
                AppTheme {
                    Box(modifier = Modifier.padding(0.dp)) {
                        AlertDialogContent(
                            title = title ?: state.title.value,
                            showClose = properties.showClose,
                            showDrag = properties.showDrag,
                            onClose = {
                                hideDialog()
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    closeButtonClick?.invoke()
                                }
                            },
                            icon = icon,
                            message = message ?: state.message.value,
                            spannedMessage = spannedMessage,
                            messageAlign = if (properties.messageJustify) TextAlign.Justify else properties.messageAlign,
                            subMessage = subMessage,
                            subSpannedMessage = spannedSubMessage,
                            subMessageAlign = properties.subMessageAlign,
                            positiveButtonTitle = positiveButton,
                            hasPositiveButton = positiveButton != null || positiveButtonClick != null,
                            positiveButtonCallback = {
                                hideDialog()
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    positiveButtonClick?.invoke()
                                }
                            },
                            negativeButtonTitle = negativeButton,
                            hasNegativeButton = negativeButton != null || negativeButtonClick != null,
                            negativeButtonCallback = {
                                hideDialog()
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    negativeButtonClick?.invoke()
                                }
                            }
                        )
                    }
                }
            }
        }

        AlertDialogProperties.ALERT_DIALOG -> {
            val dialogProperties = DialogProperties(
                dismissOnBackPress = properties.canDismiss,
                dismissOnClickOutside = properties.canDismiss,
                usePlatformDefaultWidth = false
            )
            BasicAlertDialog(
                onDismissRequest = {
                    if (properties.canDismiss) {
                        hideDialog()
                        closeButtonClick?.invoke()
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                properties = dialogProperties
            ) {
                AppTheme {
                    Box {
                        AlertDialogContent(
                            title = title ?: state.title.value,
                            showClose = properties.showClose,
                            showDrag = false,
                            onClose = {
                                hideDialog()
                                closeButtonClick?.invoke()
                            },
                            icon = icon,
                            message = message ?: state.message.value,
                            spannedMessage = spannedMessage,
                            messageAlign = if (properties.messageJustify) TextAlign.Justify else properties.messageAlign,
                            subMessage = subMessage,
                            subSpannedMessage = spannedSubMessage,
                            subMessageAlign = properties.subMessageAlign,
                            positiveButtonTitle = positiveButton,
                            hasPositiveButton = positiveButton != null || positiveButtonClick != null,
                            positiveButtonCallback = {
                                hideDialog()
                                positiveButtonClick?.invoke()
                            },
                            hasNegativeButton = negativeButton != null || negativeButtonClick != null,
                            negativeButtonTitle = negativeButton,
                            negativeButtonCallback = {
                                hideDialog()
                                negativeButtonClick?.invoke()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Alert Dialog - Default")
@Composable
fun PreviewPowerlyDialog_AlertDialog_Default() {
    MyAlertDialog(
        title = "Alert Dialog",
        message = "This is a default alert dialog.",
        onDismiss = {},
        state = rememberAlertDialogState(visible = true),
        properties = AlertDialogProperties(type = AlertDialogProperties.ALERT_DIALOG)
    )
}

@Preview(showBackground = true, name = "Alert Dialog - Custom")
@Composable
fun PreviewPowerlyDialog_AlertDialog_Custom() {
    MyAlertDialog(
        title = "Custom Alert",
        message = "This is a custom alert dialog with more options.",
        subMessage = "This is a sub-message.",
        icon = android.R.drawable.ic_dialog_info,
        positiveButton = "OK",
        positiveButtonClick = { println("OK clicked") },
        negativeButton = "Cancel",
        negativeButtonClick = { println("Cancel clicked") },
        closeButtonClick = { println("Close clicked") },
        onDismiss = { println("Dialog dismissed") },
        state = rememberAlertDialogState(visible = true),
        properties = AlertDialogProperties(
            type = AlertDialogProperties.ALERT_DIALOG,
            messageAlign = TextAlign.Start,
            messageJustify = true,
            canDismiss = true,
            showClose = true
        )
    )
}

@Preview(showBackground = true, name = "Alert Dialog - Spanned Message")
@Composable
fun PreviewPowerlyDialog_AlertDialog_SpannedMessage() {
    val spannedMessage: Spanned = SpannableString("This is a spanned message with color.").apply {
        setSpan(
            ForegroundColorSpan(Color.Red.hashCode()),
            25,
            31,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    MyAlertDialog(
        title = "Spanned Alert",
        spannedMessage = spannedMessage,
        onDismiss = {},
        state = rememberAlertDialogState(visible = true),
        properties = AlertDialogProperties(type = AlertDialogProperties.ALERT_DIALOG)
    )
}

@Preview(showBackground = true, name = "Bottom Dialog - Default")
@Composable
fun PreviewPowerlyDialog_BottomDialog_Default() {
    MyAlertDialog(
        title = "Bottom Sheet",
        message = "This is a default bottom sheet dialog.",
        onDismiss = {},
        state = rememberAlertDialogState(visible = true),
        properties = AlertDialogProperties(type = AlertDialogProperties.MODAL_DIALOG)
    )
}

@Preview(showBackground = true, name = "Bottom Dialog - Custom")
@Composable
fun PreviewPowerlyDialog_BottomDialog_Custom() {
    MyAlertDialog(
        title = "Custom Bottom Sheet",
        message = "This is a custom bottom sheet dialog with more options.",
        subMessage = "This is a sub-message in the bottom sheet.",
        icon = android.R.drawable.ic_dialog_alert,
        positiveButton = "Confirm",
        positiveButtonClick = { println("Confirm clicked") },
        negativeButton = "Dismiss",
        negativeButtonClick = { println("Dismiss clicked") },
        closeButtonClick = { println("Close clicked") },
        onDismiss = { println("Dialog dismissed") },
        state = rememberAlertDialogState(visible = true),
        properties = AlertDialogProperties(
            type = AlertDialogProperties.MODAL_DIALOG,
            messageAlign = TextAlign.End,
            canDrag = false,
            showClose = true,
            showDrag = true
        )
    )
}

@Preview(showBackground = true, name = "Bottom Dialog - Spanned Message")
@Composable
fun PreviewPowerlyDialog_BottomDialog_SpannedMessage() {
    val spannedMessage: Spanned =
        SpannableString("Spanned message with different color for bottom sheet").apply {
            setSpan(
                ForegroundColorSpan(Color.Blue.hashCode()),
                29,
                42,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    MyAlertDialog(
        title = "Spanned Bottom Sheet",
        spannedMessage = spannedMessage,
        onDismiss = {},
        state = rememberAlertDialogState(visible = true),
        properties = AlertDialogProperties(type = AlertDialogProperties.MODAL_DIALOG)
    )
}