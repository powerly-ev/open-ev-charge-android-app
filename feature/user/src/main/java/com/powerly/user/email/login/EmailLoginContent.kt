package com.powerly.user.email.login

import android.util.Patterns
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.MyTextField
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.theme.AppTheme

@Preview
@Composable
private fun EmailLoginScreenPreview() {
    AppTheme {
        EmailLoginScreenContent(
            userEmail = "",
            onBack = {},
            onContinue = {}
        )
    }
}

@Composable
internal fun EmailLoginScreenContent(
    screenState: ScreenState = rememberScreenState(),
    userEmail: String,
    onContinue: (email: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(userEmail) }
    var invalidEmail by remember { mutableStateOf(false) }

    fun validateEmail(): Boolean {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true
        else invalidEmail = true
        return false
    }

    MyScreen(
        screenState = screenState,
        horizontalAlignment = Alignment.Start,
        header = {
            ScreenHeader(
                title = stringResource(id = R.string.login_email_title),
                closeIcon = R.drawable.arrow_back,
                onClose = onBack
            )
        },
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.login_email),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(id = R.string.login_email_enter_msg),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        MyTextField(
            value = email,
            label = stringResource(R.string.login_email_enter),
            onValueChange = {
                email = it
                if (it.isNotBlank()) invalidEmail = false
            },
            error = {
                if (email.isBlank()) ""
                else context.getString(R.string.login_email_error_msg)
            },
            isError = { invalidEmail },
            showKeyboard = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentType = ContentType.EmailAddress
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.continue_),
            background = MaterialTheme.colorScheme.secondary,
            color = Color.White,
            onClick = {
                if (validateEmail()) onContinue(email)
            }
        )
    }
}

