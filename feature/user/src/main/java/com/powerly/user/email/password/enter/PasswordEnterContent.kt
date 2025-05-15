package com.powerly.user.email.password.enter

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.user.email.PASSWORD_MIN_LENGTH
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.MyPasswordTextField
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.onClick
import com.powerly.ui.theme.AppTheme

@Preview
@Composable
private fun PasswordEnterScreenPreview() {
    AppTheme {
        PasswordEnterScreenContent(

            userPassword = "",
            userEmail = "m.aly@gasable.com",
            onBack = {},
            onContinue = {},
            onForgetPassword = {}
        )
    }
}

@Composable
internal fun PasswordEnterScreenContent(
    screenState: ScreenState = rememberScreenState(),
    userEmail: String,
    userPassword: String,
    onContinue: (password: String) -> Unit,
    onForgetPassword: () -> Unit,
    onBack: () -> Unit
) {
    var password by remember { mutableStateOf(userPassword) }
    var invalidPassword by remember { mutableStateOf(false) }

    fun validatePassword(): Boolean {
        if (password.length < PASSWORD_MIN_LENGTH) {
            invalidPassword = true
            return false
        }
        return true
    }

    MyScreen(
        screenState = screenState,
        horizontalAlignment = Alignment.Start,
        header = {
            ScreenHeader(
                title = userEmail,
                closeIcon = R.drawable.arrow_back,
                onClose = onBack
            )
        },
        spacing = 8.dp,
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.login_email_password),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        MyPasswordTextField(
            value = password,
            label = stringResource(R.string.login_email_password_enter),
            onValueChange = {
                password = it
                if (it.isNotBlank()) invalidPassword = false
            },
            error = {
                if (password.length < PASSWORD_MIN_LENGTH) stringResource(
                    R.string.login_email_password_length_msg,
                    PASSWORD_MIN_LENGTH
                ) else ""
            },
            showKeyboard = true,
            isError = { invalidPassword },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentType = ContentType.Password
                },
        )

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.login_option_title),
            background = MaterialTheme.colorScheme.secondary,
            color = Color.White,
            onClick = {
                if (validatePassword()) onContinue(password)
            }
        )

        Text(
            text = stringResource(id = R.string.login_email_password_forget),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .onClick(onForgetPassword)
        )
    }
}
