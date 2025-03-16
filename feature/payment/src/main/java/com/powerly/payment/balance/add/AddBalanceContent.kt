package com.powerly.payment.balance.add

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.powerly.core.model.payment.BalanceItem
import com.powerly.core.model.payment.StripCard
import com.powerly.resources.R
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.containers.MySurface
import com.powerly.ui.containers.MySurfaceColumn
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.extensions.asPadding
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.MyIcon
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun AddBalanceScreenPreview() {
    val balanceItem = BalanceItem(
        id = 0,
        price = 10.0,
        bonus = 2.0,
        popular = true
    ).apply { currency = "SAR" }

    val stripCard = StripCard(
        paymentOption = "Visa",
        cardNumber = "42424",
        id = "1",
        default = true
    )

    AppTheme {
        AddBalanceScreenContent(
            balanceItem = balanceItem,
            paymentMethod = { stripCard },
            onClose = {},
            onAddBalance = {},
            onSelectMethod = {}
        )
    }
}

@Composable
internal fun AddBalanceScreenContent(
    screenState: ScreenState = rememberScreenState(),
    balanceItem: BalanceItem,
    paymentMethod: () -> StripCard?,
    onSelectMethod: () -> Unit,
    onAddBalance: () -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        screenState = screenState,
        modifier = Modifier.padding(0.dp),
        horizontalAlignment = Alignment.Start,
        background = Color.White,
        verticalScroll = true,
        spacing = 0.dp,
        header = {
            ScreenHeader(
                title = stringResource(R.string.balance_add),
                onClose = onClose
            )
        },
        footerPadding = 16.dp.asPadding,
        footer = {
            ButtonLarge(
                text = stringResource(R.string.agree_and_continue),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                background = MaterialTheme.colorScheme.secondary,
                icon = R.drawable.arrow_right,
                onClick = onAddBalance
            )
        }
    ) {
        Spacer(Modifier.height(8.dp))
        SectionBalance(balanceItem)
        SectionDetail(balanceItem)
        SectionPaymentMethod(
            modifier = Modifier.padding(horizontal = 16.dp),
            payment = paymentMethod,
            changeMethod = onSelectMethod
        )
        Text(
            text = stringResource(R.string.balance_non_refundable),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun SectionBalance(balanceItem: BalanceItem) {
    MySurfaceColumn(
        modifier = Modifier.padding(16.dp),
        spacing = 16.dp
    ) {
        MyRow(spacing = 8.dp) {
            Text(
                text = balanceItem.price.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = balanceItem.currency,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.weight(1f))
            ButtonSmall(
                text = "+ ${balanceItem.bonus} ${balanceItem.currency}",
                color = Color.White,
                background = MaterialTheme.colorScheme.primary,
                enabled = { false }
            )
        }
        HorizontalDivider(color = MyColors.dividerColor)
        ItemBalance(
            title = R.string.balance_add,
            value = balanceItem.price,
            currency = balanceItem.currency,
            valueFontWeight = FontWeight.Medium
        )
        ItemBalance(
            title = R.string.balance_add_message,
            value = balanceItem.totalBalance,
            currency = balanceItem.currency,
            color = MaterialTheme.colorScheme.primary,
            valueFontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionDetail(balanceItem: BalanceItem) {
    MyColumn(
        spacing = 8.dp,
        modifier = Modifier.padding(16.dp)
    ) {
        ItemBalance(
            title = R.string.order_subtotal,
            value = balanceItem.price,
            currency = balanceItem.currency,
            color = MaterialTheme.colorScheme.secondary,
        )
        ItemBalance(
            title = R.string.order_discount,
            value = balanceItem.bonus,
            icon = R.drawable.ic_discount,
            currency = balanceItem.currency,
            color = MaterialTheme.colorScheme.primary,
        )
        ItemBalance(
            title = R.string.order_vat,
            value = balanceItem.vat,
            currency = balanceItem.currency,
            color = MaterialTheme.colorScheme.secondary,
        )
        ItemBalance(
            title = R.string.order_total_price,
            value = balanceItem.totalBalance,
            currency = balanceItem.currency,
            color = MaterialTheme.colorScheme.primary,
            titleFontWeight = FontWeight.Medium,
            valueFontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionPaymentMethod(
    payment: () -> StripCard?,
    changeMethod: () -> Unit,
    modifier: Modifier = Modifier,
    canChange: Boolean = true,
) {
    val (title, icon) = paymentMethodDetails(payment(), false)

    MySurfaceRow(
        modifier = modifier,
        color = Color.Transparent,
        cornerRadius = 0.dp,
        onClick = if (canChange) changeMethod else null,
    ) {
        MySurface(modifier = Modifier.padding(4.dp)) {
            Image(
                painter = painterResource(icon),
                modifier = Modifier.size(24.dp),
                contentDescription = ""
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
        ButtonSmall(
            text = stringResource(R.string.change),
            color = MaterialTheme.colorScheme.secondary,
            background = MaterialTheme.colorScheme.surface,
            onClick = changeMethod
        )
    }
}

@Composable
private fun ItemBalance(
    @StringRes title: Int,
    value: Any,
    currency: String,
    @DrawableRes icon: Int? = null,
    color: Color = MaterialTheme.colorScheme.secondary,
    titleFontWeight: FontWeight = FontWeight.Normal,
    valueFontWeight: FontWeight = FontWeight.Normal

) {
    MyRow(spacing = 4.dp) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = titleFontWeight
        )
        Spacer(Modifier.weight(1f))
        icon?.let {
            MyIcon(
                icon = it,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = valueFontWeight
        )
        Text(
            text = currency,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = valueFontWeight
        )
    }
}