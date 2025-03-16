package com.powerly.payment.balance.withdraw

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.payment.Wallet
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.containers.MySurface
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.onClick
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

private const val TAG = "WithdrawScreenContent"

@Preview
@Composable
private fun WithdrawScreenPreview() {
    val wallets = listOf(
        Wallet(
            id = 123,
            name = "Customer Wallet",
            balance = 0.0
        ),
        Wallet(
            id = 456,
            name = "Seller Wallet",
            balance = 130.0,
            withdrawable = false
        )
    )
    AppTheme {
        WithdrawScreenContent(
            currency = "USD",
            wallets = { wallets },
            uiEvents = {}
        )
    }
}

@Composable
internal fun WithdrawScreenContent(
    wallets: () -> List<Wallet>,
    currency: String,
    screenState: ScreenState= rememberScreenState(),
    uiEvents: (WithdrawEvents) -> Unit
) {
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }

    LaunchedEffect(wallets().size) {
        selectedWallet = wallets().firstOrNull { it.withdrawable }
    }

    MyScreen(
        screenState = screenState,
        header = {
            ScreenHeader(
                title = stringResource(R.string.wallet),
                onClose = { uiEvents(WithdrawEvents.Close) }
            )
        },
        background = Color.White,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(16.dp),
        spacing = 24.dp,
        footer = {
            Footer(
                withdrawEnabled = { (selectedWallet?.balance ?: 0.0) > 0.0 },
                uiEvents = uiEvents
            )
        }
    ) {
        MySurface(modifier = Modifier.padding(4.dp)) {
            Text(
                text = stringResource(R.string.wallet_withdraw_from),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        wallets().forEach {
            WithdrawOption(
                wallet = it.apply { this.currency = currency },
                onSelect = { selectedWallet = it }
            )
        }
    }
}


@Composable
private fun Footer(
    withdrawEnabled: () -> Boolean,
    uiEvents: (WithdrawEvents) -> Unit
) {
    MyColumn(spacing = 16.dp, modifier = Modifier.padding(16.dp)) {
        SectionTermsAndConditions(
            openPrivacyPolicy = {
                uiEvents(WithdrawEvents.PrivacyPolicy)
            },
            openTermsOfService = {
                uiEvents(WithdrawEvents.TermsOfService)
            }
        )

        ButtonLarge(
            text = stringResource(R.string.wallet_withdraw_agree),
            enabled = withdrawEnabled,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            disabledBackground = MyColors.grey500,
            background = MaterialTheme.colorScheme.secondary,
            onClick = { uiEvents(WithdrawEvents.Withdraw) }
        )
    }
}

@Composable
private fun WithdrawOption(
    wallet: Wallet,
    onSelect: () -> Unit
) {
    MyRow(
        spacing = 16.dp,
        modifier = Modifier.onClick(
            onSelect,
            enabled = wallet.withdrawable
        )
    ) {
        IconWallet()
        MyColumn(spacing = 4.dp) {
            Text(
                text = wallet.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "${wallet.balance} ${wallet.currency}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (wallet.withdrawable) RadioButton(
            modifier = Modifier.size(20.dp),
            selected = true,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            ),
            onClick = onSelect
        )
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun IconWallet() {
    MySurface(modifier = Modifier.size(40.dp)) {
        Image(
            painter = painterResource(R.drawable.ic_payment_cash_logo),
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center)
        )
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
            text = stringResource(R.string.wallet_policy_read_our),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(R.string.wallet_policy),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.onClick(openPrivacyPolicy)
        )
        Text(
            text = stringResource(R.string.wallet_policy_tab_withdraw),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(R.string.wallet_policy_terms),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.onClick(openTermsOfService)
        )
    }
}
