package com.powerly.account.profile.dialogs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.model.location.AppCurrency
import com.powerly.ui.dialogs.ItemOptionsMenu
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.MyDropdownMenu
import com.powerly.ui.dialogs.ProgressView
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Run interactive preview to see menu items
 */
@Preview
@Composable
private fun CurrencyDropMenuPreview() {
    val list = listOf(
        AppCurrency("USD"),
        AppCurrency("EUR"),
        AppCurrency("EGP"),
        AppCurrency("JP")
    )
    val status = CurrenciesStatus.Success(list)
    val flow = flow { emit(status) }
    AppTheme {
        CurrencyDropMenu(
            currenciesFlow = flow,
            onSelectCurrency = {},
            state = rememberMyDialogState(),
        )
    }
}

@Composable
internal fun CurrencyDropMenu(
    state: MyDialogState,
    currenciesFlow: Flow<CurrenciesStatus>,
    onSelectCurrency: (AppCurrency) -> Unit
) {
    Surface(shape = RoundedCornerShape(16.dp)) {
        MyDropdownMenu(
            dialogState = state,
            spacing = 16.dp,
            maxHeight = 0.4f,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            val currenciesStatus = currenciesFlow.collectAsState(CurrenciesStatus.Loading)
            when (val it = currenciesStatus.value) {
                is CurrenciesStatus.Success -> {
                    it.data.forEach { currency ->
                        ItemOptionsMenu(
                            title = currency.iso,
                            onClick = {
                                onSelectCurrency(currency)
                                state.dismiss()
                            }
                        )
                    }
                }

                is CurrenciesStatus.Loading -> {
                    ProgressView()
                }

                else -> {}
            }
        }
    }
}