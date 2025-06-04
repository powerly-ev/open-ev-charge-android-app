package com.powerly.ui.dialogs.alert

import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.containers.MySurfaceColumn
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.screen.DialogHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors.borderColor

@Preview
@Composable
private fun AlertDialogPreview1() {
    AppTheme {
        MyColumn {
            val context = LocalContext.current
            val msg = """
                    ${context.getString(R.string.profile_confirm_delete_account)} 
                    ${context.getString(R.string.profile_delete_account_description)}
                    """.trimIndent()
            AlertDialogContent(
                title = "Title",
                onClose = {},
                message = msg,
                messageAlign = TextAlign.Justify
            )
            AlertDialogContent(
                showDrag = true,
                message = "Dialog Message"
            )
            AlertDialogContent(
                title = "Title",
                onClose = {},
                message = "Dialog Message",
                positiveButtonTitle = "Yes"
            )
        }
    }
}

@Preview
@Composable
private fun AlertDialogPreview2() {
    AppTheme {
        MyColumn {
            AlertDialogContent(
                title = "Title",
                onClose = {},
                message = "Dialog Message",
                positiveButtonTitle = "Yes",
                negativeButtonTitle = "No"
            )
            AlertDialogContent(
                title = "Title",
                onClose = {},
                icon = R.drawable.logo,
                message = "Message",
                subMessage = "Sub Message",
                positiveButtonTitle = "Yes",
                negativeButtonTitle = "No"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlertDialogContent(
    title: String? = null,
    onClose: () -> Unit = {},
    showClose: Boolean = true,
    message: String? = null,
    spannedMessage: Spanned? = null,
    messageAlign: TextAlign = TextAlign.Center,
    subMessage: String? = null,
    subSpannedMessage: Spanned? = null,
    subMessageJustify: Boolean = false,
    subMessageAlign: TextAlign = if (subMessageJustify) TextAlign.Justify
    else TextAlign.Center,
    @DrawableRes icon: Int? = null,
    showDrag: Boolean = false,
    positiveButtonTitle: String? = null,
    hasPositiveButton: Boolean = false,
    positiveButtonCallback: (() -> Unit)? = null,
    hasNegativeButton: Boolean = false,
    negativeButtonTitle: String? = null,
    negativeButtonCallback: (() -> Unit)? = null
) {

    MySurfaceColumn(
        cornerRadius = 16.dp,
        spacing = 0.dp,
        color = Color.White,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (showDrag) {
            BottomSheetDefaults.DragHandle()
        }
        title?.let {
            DialogHeader(
                title = it,
                showDivider = true,
                layoutDirection = LayoutDirection.Rtl,
                onClose = onClose,
                closeIcon = if (showClose) R.drawable.close
                else null
            )
        }
        MyColumn(
            spacing = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = "",
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = messageAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            spannedMessage?.let {
                Text(
                    text = SpannableStringBuilder(it).toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = messageAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            subMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = subMessageAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            subSpannedMessage?.let {
                Text(
                    text = SpannableStringBuilder(it).toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = subMessageAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(0.dp))
            SectionButtons(
                positiveButtonTitle,
                hasPositiveButton,
                positiveButtonCallback,
                negativeButtonTitle,
                hasNegativeButton,
                negativeButtonCallback
            )
        }
    }
}

@Composable
private fun SectionButtons(
    positiveButtonTitle: String? = null,
    hasPositiveButton: Boolean,
    positiveButtonCallback: (() -> Unit)? = null,
    negativeButtonTitle: String? = null,
    hasNegativeButton: Boolean,
    negativeButtonCallback: (() -> Unit)? = null,
) {
    val positiveButtonTitle = when {
        positiveButtonTitle != null -> positiveButtonTitle
        hasPositiveButton -> stringResource(R.string.dialog_ok)
        else -> null
    }
    val negativeButtonTitle = when {
        negativeButtonTitle != null -> negativeButtonTitle
        hasNegativeButton -> stringResource(R.string.dialog_cancel)
        else -> null
    }

    if (negativeButtonTitle == null &&
        positiveButtonTitle == null
    ) return

    val buttonWeight = remember {
        if (positiveButtonTitle != null &&
            negativeButtonTitle != null
        ) 0.5f
        else 1f
    }

    MyRow(
        modifier = Modifier.fillMaxWidth(),
        spacing = 16.dp
    ) {
        negativeButtonTitle?.let {
            ButtonLarge(
                text = it,
                color = MaterialTheme.colorScheme.secondary,
                background = Color.White,
                cornerRadius = 16.dp,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.weight(buttonWeight),
                onClick = negativeButtonCallback
            )
        }
        positiveButtonTitle?.let {
            ButtonLarge(
                text = it,
                background = MaterialTheme.colorScheme.secondary,
                color = Color.White,
                cornerRadius = 16.dp,
                modifier = Modifier.weight(buttonWeight),
                onClick = positiveButtonCallback
            )
        }
    }
}