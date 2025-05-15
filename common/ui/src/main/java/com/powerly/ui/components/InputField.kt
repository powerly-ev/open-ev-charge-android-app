package com.powerly.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.LocalAutofillHighlightColor
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.delay

@Preview
@Composable
private fun MyTextFieldPreview() {
    AppTheme {
        Surface(color = Color.White) {
            Column(Modifier.padding(16.dp)) {
                MyTextField(
                    value = "",
                    placeholder = "Multiline input",
                    modifier = Modifier.fillMaxWidth(),
                    lines = 5,
                    onValueChange = {}
                )
                MyTextField(
                    value = "",
                    label = "Single line input",
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {}
                )
                MyPasswordTextField(
                    value = "",
                    label = "Write Password",
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {}
                )
            }
        }
    }
}

@Composable
fun MyTextField(
    value: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    label: String? = null,
    lines: Int = 1,
    enabled: Boolean = true,
    maxLines: Int = lines,
    cornerRadius: Dp = 8.dp,
    trailingView: @Composable (() -> Unit)? = null,
    error: @Composable () -> String = { "" },
    isError: () -> Boolean = { false },
    showKeyboard: Boolean = false,
    focusedBorderColor: Color = MaterialTheme.colorScheme.secondary,
    unfocusedBorderColor: Color = MyColors.borderColor,
    highlightColor: Color = Color.Transparent,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit,
) {

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // LaunchedEffect prevents endless focus request
    LaunchedEffect(focusRequester) {
        if (showKeyboard) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
        }
    }

    CompositionLocalProvider(LocalAutofillHighlightColor provides highlightColor) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.focusRequester(focusRequester),
            shape = RoundedCornerShape(cornerRadius),
            minLines = lines,
            maxLines = maxLines,
            trailingIcon = trailingView,
            isError = isError(),
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            supportingText = {
                if (isError()) Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error(),
                    color = MaterialTheme.colorScheme.error
                )
            },
            label = {
                label?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = unfocusedBorderColor,
            ),
        )
    }
}

@Composable
fun MyPasswordTextField(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: @Composable () -> String = { "" },
    isError: () -> Boolean = { false },
    showKeyboard: Boolean = false,
    focusedBorderColor: Color = MaterialTheme.colorScheme.secondary,
    unfocusedBorderColor: Color = MyColors.borderColor,
    highlightColor: Color = Color.Transparent,
    onValueChange: (String) -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // LaunchedEffect prevents endless focus request
    LaunchedEffect(focusRequester) {
        if (showKeyboard) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
        }
    }
    CompositionLocalProvider(LocalAutofillHighlightColor provides highlightColor) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.focusRequester(focusRequester),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            isError = isError(),
            keyboardOptions = keyboardOptions,
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            suffix = {
                MyIcon(
                    icon = if (showPassword.not()) R.drawable.password_show
                    else R.drawable.password_hide,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showPassword = showPassword.not() }
                )
            },
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            supportingText = {
                if (isError()) Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            label = {
                label?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = unfocusedBorderColor,
            ),
        )
    }
}

@Preview
@Composable
private fun MySearchBoxPreview() {
    AppTheme {
        MySearchBox(hint = R.string.location_search)
    }
}

@Composable
fun MySearchBox(
    @StringRes hint: Int,
    showKeyboard: Boolean = false,
    showDivider: Boolean = false,
    cornerRadius: Dp = 8.dp,
    background: Color = MaterialTheme.colorScheme.surface,
    iconColor: Color = MaterialTheme.colorScheme.secondary,
    onQueryChanges: ((String) -> Unit)? = null,
    afterQueryChanges: ((String) -> Unit)? = null
) {
    var query by remember { mutableStateOf<String?>(null) }
    val isShowKeyboard = remember { mutableStateOf(showKeyboard) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // LaunchedEffect prevents endless focus request
    LaunchedEffect(focusRequester) {
        if (isShowKeyboard.equals(true)) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
        }
    }

    afterQueryChanges?.let {
        LaunchedEffect(key1 = query) {
            delay(1000)
            query?.let {
                if (it.isBlank() || it.length >= 2)
                    afterQueryChanges.invoke(query!!)
            }
        }
    }

    Column {
        MySurfaceRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            cornerRadius = cornerRadius,
            spacing = 0.dp,
            color = background
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = hint),
                tint = iconColor
            )

            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = query.orEmpty(),
                singleLine = true,
                onValueChange = {
                    query = it
                    onQueryChanges?.invoke(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(id = hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MyColors.subColor
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    focusedPlaceholderColor = MyColors.subColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
        if (showDivider) HorizontalDivider(
            thickness = 1.dp,
            color = MyColors.dividerColor
        )
    }
}
