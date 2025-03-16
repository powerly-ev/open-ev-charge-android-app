package com.powerly.payment.balance.show

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.resources.R
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.dialogs.ProgressView
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun ShowBalanceScreenPreview() {
    val balanceItems = listOf(
        BalanceItem(id = 0, price = 10.0, popular = true),
        BalanceItem(id = 0, price = 50.0, popular = false),
    )
    AppTheme {
        ShowBalanceScreenContent(
            balanceState = ApiStatus.Success(balanceItems),
            currency = "SAR",
            balance = 150.0,
            onClose = {},
            onAddBalance = {}
        )
    }
}

@Composable
internal fun ShowBalanceScreenContent(
    balanceState: ApiStatus<List<BalanceItem>>,
    currency: String,
    balance: Double,
    onAddBalance: (BalanceItem) -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        background = Color.White,
        verticalScroll = true,
        header = {
            ScreenHeader(
                title = stringResource(R.string.balance_show),
                onClose = onClose
            )
        }
    ) {
        SectionBalanceAvailable(
            balance = balance,
            currency = currency
        )
        Spacer(Modifier.height(8.dp))
        when (balanceState) {
            is ApiStatus.Loading -> ProgressView()
            is ApiStatus.Success -> {
                SectionBalanceList(
                    currency = currency,
                    balanceItems = balanceState.data,
                    onAddBalance = onAddBalance
                )
            }

            else -> {}
        }

    }
}

@Composable
private fun SectionBalanceAvailable(
    balance: Double,
    currency: String
) {
    Text(
        text = stringResource(R.string.balance_available),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary
    )
    MySurfaceRow(
        color = Color.Transparent,
        border = BorderStroke(2.dp, MyColors.borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        spacing = 8.dp
    ) {
        Text(
            text = balance.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = currency,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun SectionBalanceList(
    currency: String,
    balanceItems: List<BalanceItem>,
    onAddBalance: (BalanceItem) -> Unit
) {
    Text(
        text = stringResource(R.string.balance_add),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary
    )
    balanceItems.forEach {
        ItemBalance(
            balance = it,
            currency = currency,
            onClick = { onAddBalance(it) }
        )
    }
}

@Composable
private fun ItemBalance(
    balance: BalanceItem,
    currency: String,
    onClick: () -> Unit
) {
    MySurfaceRow(
        cornerRadius = 8.dp,
        spacing = 8.dp,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = balance.price.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = currency,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(Modifier.weight(1f))
        if (balance.popular) Surface(
            color = MyColors.greenLight,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = stringResource(R.string.balance_popular),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(6.dp)
            )
        }
        Spacer(Modifier.width(24.dp))
    }
}