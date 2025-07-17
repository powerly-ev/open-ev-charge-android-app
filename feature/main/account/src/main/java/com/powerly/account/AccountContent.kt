package com.powerly.account

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.ui.HomeUiState
import com.powerly.resources.R
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.components.MyIcon
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.SectionBalance
import com.powerly.ui.extensions.isArabic
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors


@Preview(locale = "en")
@Composable
private fun AccountScreenPreview() {
    val uiState = HomeUiState().apply {
        isLoggedIn.value = true
    }
    AppTheme {
        AccountScreenContent(uiState, uiEvents = {})
    }
}


@Composable
internal fun AccountScreenContent(
    uiState: HomeUiState,
    uiEvents: (AccountEvents) -> Unit
) {
    val isLoggedIn by remember { uiState.isLoggedIn }
    val userName by remember { uiState.userName }
    val balance by remember { uiState.balance }
    val currency by remember { uiState.currency }
    val language by remember { uiState.languageName }
    val appVersion = remember { uiState.appVersion }

    MyScreen(
        header = {
            SectionHeader(
                userName = userName,
                balance = balance,
                currency = currency,
                isLoggedIn = isLoggedIn,
                openProfile = { uiEvents(AccountEvents.OpenProfile) },
                openBalance = { uiEvents(AccountEvents.OpenBalance) }
            )
        },
        modifier = Modifier.padding(0.dp),
        background = Color.White,
        spacing = 0.dp
    ) {
        HorizontalDivider(color = MyColors.dividerColor)
        if (isLoggedIn) SectionTab(
            icon = R.drawable.ic_sd_wallet,
            title = R.string.wallet,
            onClick = { uiEvents(AccountEvents.OpenWallet) }
        )
        if (isLoggedIn) SectionTab(
            icon = R.drawable.car_electric,
            title = R.string.account_vehicles,
            onClick = { uiEvents(AccountEvents.OpenVehicles) }
        )
        SectionTab(
            icon = R.drawable.ic_baseline_support_agent_24,
            title = R.string.support,
            onClick = { uiEvents(AccountEvents.OpenSupport) }
        )
        if (isLoggedIn) SectionTab(
            icon = R.drawable.ic_sd_invite,
            title = R.string.invite,
            onClick = { uiEvents(AccountEvents.OpenInvite) }
        )
        SectionTab(
            icon = R.drawable.ic_sd_language,
            title = R.string.language,
            actionButton = language,
            onClick = { uiEvents(AccountEvents.LanguageDialog) }
        )

        SectionAppVersion(appVersion)
    }
}


@Composable
private fun SectionTab(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    actionButton: String? = null,
    onClick: () -> Unit
) {
    MyRow(
        spacing = 16.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 32.dp)
    ) {
        MyIcon(icon = icon, modifier = Modifier.size(25.dp))
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        actionButton?.let { title ->
            Spacer(modifier = Modifier.weight(1f))
            ButtonSmall(text = title, onClick = onClick)
        }
    }
}


@Composable
private fun SectionHeader(
    userName: String,
    balance: String,
    currency: String,
    isLoggedIn: Boolean,
    openProfile: () -> Unit,
    openBalance: () -> Unit
) {
    MyRow(
        modifier = Modifier
            .background(Color.White)
            .clickable(onClick = { if (isLoggedIn) openProfile() })
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = userName,
            modifier = Modifier.size(60.dp)
        )
        MyColumn(spacing = if (isArabic()) 0.dp else 8.dp) {
            Text(
                text = userName.ifBlank { stringResource(R.string.profile_guest) },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            if (isLoggedIn) Text(
                text = stringResource(id = R.string.profile_see),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (isLoggedIn) {
            Spacer(modifier = Modifier.weight(1f))
            SectionBalance(
                balance = balance,
                currency = currency,
                onClick = openBalance,
                background = Color.White
            )
        }
    }
}

@Composable
private fun SectionAppVersion(version: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = version,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.tertiary
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(color = MyColors.dividerColor)
}
