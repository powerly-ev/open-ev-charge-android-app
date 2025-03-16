package com.powerly.payment.methods.select

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.payment.StripCard
import com.powerly.payment.balance.add.paymentMethodDetails
import com.powerly.resources.R
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.containers.MySurface
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.components.MyIcon
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.onClick
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors.dividerColor

@Preview
@Composable
private fun PaymentMethodsScreenPreview() {
    val methods = listOf(
        StripCard("0", "3148", "VISA", true),
        StripCard("1", "9999", "Master", false),
        StripCard("3", "1223", "MADA", false)
    )
    AppTheme {
        PaymentMethodsScreenContent(
            methods = { methods },
            onAdd = {},
            onSelect = {},
            onClose = {}
        )
    }
}

@Composable
internal fun PaymentMethodsScreenContent(
    screenState: ScreenState = rememberScreenState(),
    methods: () -> List<StripCard>,
    onSelect: (StripCard) -> Unit,
    onAdd: () -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        modifier = Modifier.padding(16.dp),
        background = Color.White,
        spacing = 8.dp,
        screenState = screenState,
        verticalScroll = true,
        header = {
            ScreenHeader(
                title = stringResource(R.string.order_payment_methods),
                onClose = onClose,
                showDivider = false
            )
        }
    ) {
        methods().forEach {
            ItemPaymentMethod(
                method = it,
                onSelect = { onSelect(it) }
            )
        }
        Spacer(Modifier.height(8.dp))
        ButtonAddMethod(
            onClick = onAdd,
            isLoading = { screenState.loading }
        )
    }
}

@Composable
private fun ItemPaymentMethod(
    method: StripCard,
    onSelect: () -> Unit
) {
    val (title, icon) = paymentMethodDetails(method)

    MyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .onClick(onSelect),
        spacing = 8.dp
    ) {
        MyRow {
            MySurface(modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.size(32.dp)
                )
            }
            MyColumn(
                spacing = 4.dp,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
                if (method.isCard) {
                    Text(
                        text = method.cardNumberHidden,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            if (method.default) MyIcon(
                icon = R.drawable.ic_true,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider(color = dividerColor)
    }
}

@Composable
private fun ButtonAddMethod(
    onClick: () -> Unit,
    isLoading: () -> Boolean
) {
    TextButton(
        onClick = onClick,
        enabled = isLoading().not(),
        modifier = Modifier.fillMaxWidth()
    ) {
        MyIcon(
            icon = R.drawable.ic_add,
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.payment_method_add),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
    }
}
