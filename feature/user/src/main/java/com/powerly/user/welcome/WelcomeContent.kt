package com.powerly.user.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.resources.R
import com.powerly.ui.containers.MySurfaceColumn
import com.powerly.ui.dialogs.signIn.SignInButton
import com.powerly.ui.dialogs.signIn.SignInOptions
import com.powerly.ui.extensions.onClick
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.myBorder

private const val TAG = "WelcomeScreen"

@Preview
@Composable
private fun WelcomeScreenPreview() {
    AppTheme {
        WelcomeScreenContent(
            appVersion = "Version: debug 0.1",
            selectedLanguage = "Arabic",
            onShowLanguageDialog = {},
            onShowOptionsDialog = {},
            onOpenUserAgreement = {},
            signInEvents = {}
        )
    }
}


@Composable
internal fun WelcomeScreenContent(
    appVersion: String,
    selectedLanguage: String,
    onShowOptionsDialog: () -> Unit,
    onShowLanguageDialog: () -> Unit,
    onOpenUserAgreement: (Int) -> Unit,
    signInEvents: (SignInOptions) -> Unit,
) {
    MyScreen(
        spacing = 16.dp,
        modifier = Modifier.padding(
            top = 16.dp, bottom = 8.dp,
            start = 16.dp, end = 16.dp
        ),
        horizontalAlignment = Alignment.Start,
        background = Color.White,
        verticalScroll = true
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
                .size(120.dp)
        )
        Text(
            text = stringResource(id = R.string.welcome),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(id = R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        SectionLanguage(
            selectedLanguage = selectedLanguage,
            onSelectLanguage = onShowLanguageDialog
        )

        MySurfaceColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            border = myBorder,
            spacing = 16.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            SignInButton(
                title = R.string.login_option_email,
                icon = R.drawable.sign_in_email,
                color = MaterialTheme.colorScheme.secondary,
                iconTint = MaterialTheme.colorScheme.secondary,
                background = Color.White,
                border = myBorder,
                onClick = { signInEvents(SignInOptions.Email) }
            )
            SignInButton(
                title = R.string.login_option_other,
                background = Color.Transparent,
                color = MaterialTheme.colorScheme.primary,
                onClick = onShowOptionsDialog
            )
        }
        SectionTermsAndConditions(
            openPrivacyPolicy = {
                onOpenUserAgreement(1)
            },
            openTermsOfService = {
                onOpenUserAgreement(2)
            },
        )
        Text(
            text = appVersion,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionLanguage(
    selectedLanguage: String,
    onSelectLanguage: () -> Unit
) {
    Text(
        text = stringResource(id = R.string.select_app_language),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.tertiary
    )

    Surface(
        onClick = onSelectLanguage,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedLanguage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionTermsAndConditions(
    openPrivacyPolicy: () -> Unit,
    openTermsOfService: () -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(
                R.string.welcome_user_agreement,
                stringResource(id = R.string.app_name)
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(R.string.welcome_privacy_policy),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.onClick(openPrivacyPolicy)
        )
        Text(
            text = stringResource(R.string.welcome_and),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(R.string.welcome_terms_of_service),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.onClick(openTermsOfService)
        )
    }
}