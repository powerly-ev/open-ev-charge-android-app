package com.powerly.payment.methods.add

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.isPreview
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.powerly.ui.theme.myBorder
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.view.CardInputListener
import com.stripe.android.view.CardInputWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "AddPaymentScreen"

@Preview
@Composable
private fun AddPaymentScreenPreview() {
    AppTheme {
        AddPaymentScreenContent(
            cardNumber = flow { emit("") },
            onAddCard = {},
            onScanCard = {},
            onClose = {}
        )
    }
}

@Composable
internal fun AddPaymentScreenContent(
    screenState: ScreenState = rememberScreenState(),
    cardNumber: Flow<String>,
    onAddCard: (PaymentMethodCreateParams) -> Unit,
    onScanCard: () -> Unit,
    onClose: () -> Unit
) {
    var cardParms by remember {
        mutableStateOf<PaymentMethodCreateParams?>(null)
    }

    MyScreen(
        screenState = screenState,
        header = {
            ScreenHeader(
                title = stringResource(R.string.payment_card_add),
                onClose = onClose
            )
        },
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = myBorder
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            ) {
                if (isPreview().not()) CardInput(
                    cardNumber = cardNumber,
                    onFillCard = { cardParms = it }
                ) else Text(
                    text = "Card Input Preview",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterStart)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        ButtonLarge(
            text = stringResource(R.string.payment_card_add),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            background = MaterialTheme.colorScheme.secondary,
            disabledBackground = MaterialTheme.colorScheme.secondary,
            enabled = { cardParms != null },
            onClick = { cardParms?.let { onAddCard(it) } }
        )
        ButtonLarge(
            text = stringResource(R.string.payment_card_scan),
            color = MaterialTheme.colorScheme.secondary,
            layoutDirection = LayoutDirection.Rtl,
            icon = R.drawable.card_scanner,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            background = MyColors.grey200,
            onClick = onScanCard
        )
    }
}

@Composable
private fun CardInput(
    cardNumber: Flow<String>,
    onFillCard: (PaymentMethodCreateParams?) -> Unit
) {
    var inputWidget by remember { mutableStateOf<CardInputWidget?>(null) }
    var cardComplete by remember { mutableStateOf(false) }
    var cvcComplete by remember { mutableStateOf(false) }
    var codeComplete by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        cardNumber.collect { card ->
            Log.v(TAG, "cardNumber = $card")
            inputWidget?.setCardNumber(card)
            inputWidget?.requestFocus()
        }
    }

    LaunchedEffect(
        cardComplete,
        cvcComplete,
        codeComplete
    ) {
        if (cardComplete && cvcComplete && codeComplete) {
            inputWidget?.paymentMethodCreateParams?.let {
                onFillCard(it)
            }
        } else onFillCard(null)
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            CardInputWidget(context).apply {
                inputWidget = this
                postalCodeEnabled = false
                setCardInputListener(object : CardInputListener {
                    override fun onCardComplete() {
                        cardComplete = true
                    }

                    override fun onCvcComplete() {
                        cvcComplete = true
                    }

                    override fun onExpirationComplete() {
                        codeComplete = true
                    }

                    override fun onFocusChange(focusField: CardInputListener.FocusField) {
                    }

                    override fun onPostalCodeComplete() {
                    }

                })
            }
        }
    )
}