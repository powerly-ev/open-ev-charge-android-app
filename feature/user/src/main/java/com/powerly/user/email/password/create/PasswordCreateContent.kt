package com.powerly.user.email.password.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.user.email.PASSWORD_MIN_LENGTH
import com.powerly.user.email.PasswordStrength
import com.powerly.user.email.checkPasswordStrength
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.MyIcon
import com.powerly.ui.components.MyPasswordTextField
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.onClick
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun PasswordCreateScreenPreview() {
    AppTheme {
        PasswordCreateScreenContent(
            userPassword = "",
            userEmail = "m.aly@gasable.com",
            userCountry = { "Egypt" },
            onSelectCountry = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Composable
internal fun PasswordCreateScreenContent(
    screenState: ScreenState = rememberScreenState(),
    userPassword: String,
    userEmail: String,
    userCountry: () -> String?,
    onSelectCountry: () -> Unit,
    onContinue: (password: String) -> Unit,
    onBack: () -> Unit
) {
    var password by remember { mutableStateOf(userPassword) }
    var strength by remember {
        mutableStateOf(
            if (password.isBlank()) null
            else checkPasswordStrength(password)
        )
    }
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
        header = {
            ScreenHeader(
                title = stringResource(R.string.login_email_password_create_title),
                closeIcon = R.drawable.arrow_back,
                onClose = onBack,
                singleLine = true
            )
        },
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = stringResource(id = R.string.login_email_password_create),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(id = R.string.login_email_password_create_msg),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            MyPasswordTextField(
                value = password,
                label = stringResource(R.string.login_email_password_enter),
                onValueChange = {
                    password = it
                    strength = if (it.isBlank()) null
                    else checkPasswordStrength(it)
                    if (it.isNotBlank()) invalidPassword = false
                },
                isError = { invalidPassword },
                error = {
                    if (password.length < PASSWORD_MIN_LENGTH) stringResource(
                        R.string.login_email_password_length_msg,
                        PASSWORD_MIN_LENGTH
                    ) else ""
                },
                showKeyboard = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.Password
                    }
            )
            PasswordStrengthIndicator(passwordStrength = { strength })
        }

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.continue_),
            background = MaterialTheme.colorScheme.secondary,
            color = Color.White,
            onClick = {
                if (validatePassword()) onContinue(password)
            }
        )
        Spacer(Modifier.weight(1f))
        SectionCountry(
            selectCountry = onSelectCountry,
            country = userCountry
        )
    }
}

@Composable
private fun PasswordStrengthIndicator(
    passwordStrength: () -> PasswordStrength?
) {
    val strength = passwordStrength()
    val hint = when (strength) {
        PasswordStrength.WEAK ->
            R.string.login_email_password_weak

        PasswordStrength.MEDIUM ->
            R.string.login_email_password_medium

        PasswordStrength.STRONG ->
            R.string.login_email_password_strong

        else -> null
    }

    MyRow(
        modifier = Modifier.fillMaxWidth(),
        spacing = 4.dp,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyRow(
            modifier = Modifier.weight(1f),
            spacing = 2.dp
        ) {
            ItemStrengthIndicator(
                selected = listOf(
                    PasswordStrength.WEAK,
                    PasswordStrength.MEDIUM,
                    PasswordStrength.STRONG
                ).contains(strength)
            )
            ItemStrengthIndicator(
                selected = listOf(
                    PasswordStrength.MEDIUM,
                    PasswordStrength.STRONG
                ).contains(strength)
            )
            ItemStrengthIndicator(
                selected = listOf(
                    PasswordStrength.STRONG
                ).contains(strength)
            )
        }
        hint?.let {
            Text(
                text = stringResource(id = it),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

    }
}

@Composable
private fun RowScope.ItemStrengthIndicator(selected: Boolean) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MyColors.grey250
    Spacer(
        modifier = Modifier
            .weight(0.33f)
            .height(3.dp)
            .background(
                if (selected) selectedColor
                else unselectedColor
            )
    )
}

@Composable
private fun SectionCountry(
    selectCountry: () -> Unit,
    country: () -> String?
) {
    MyRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .onClick(selectCountry),
        spacing = 16.dp
    ) {
        Text(
            text = country() ?: stringResource(id = R.string.profile_select_country_please),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        MyIcon(R.drawable.ic_arrow_down)
    }
}