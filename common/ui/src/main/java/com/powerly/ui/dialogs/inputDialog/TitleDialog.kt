package com.powerly.ui.dialogs.inputDialog

import android.app.Dialog
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.dialogs.MyDialog
import com.powerly.ui.components.MyTextField
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.resources.R
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.screen.DialogHeader


@Preview
@Composable
private fun MyInputDialogPreview() {
    AppTheme {
        MyInputDialog(
            state = rememberMyDialogState(visible = true),
            default = "Test",
            title = R.string.app_name,
            hint = R.string.login_need_help,
            buttonTitle = R.string.app_name,
            onUpdate = {},
        )
    }
}

@Composable
fun MyInputDialog(
    state: MyDialogState,
    default: String = "",
    @StringRes hint: Int,
    @StringRes title: Int,
    @StringRes buttonTitle: Int,
    headerAlign: TextAlign = TextAlign.Center,
    onUpdate: (String) -> Unit
) {
    var input by remember { mutableStateOf(default) }

    MyDialog(
        state = state,
        header = {
            DialogHeader(
                title = stringResource(id = title),
                layoutDirection = LayoutDirection.Rtl,
                onClose = { state.dismiss() },
                textAlign = headerAlign
            )
        },
        spacing = 16.dp,
    ) {
        MyTextField(
            modifier = Modifier.fillMaxWidth(),
            value = input,
            onValueChange = { input = it },
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            placeholder = stringResource(id = hint)
        )

        ButtonLarge(
            text = stringResource(id = buttonTitle),
            color = Color.White,
            background = MaterialTheme.colorScheme.secondary,
            enabled = { input.isNotEmpty() },
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                state.dismiss()
                onUpdate(input)
            }
        )
    }
}


