package com.powerly.payment.presentation.paymentMethods.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.payment.PaymentManager
import com.powerly.payment.domain.repository.CardsRepository
import com.powerly.ui.dialogs.loading.initScreenState
import com.stripe.android.model.PaymentMethodCreateParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AddPaymentViewModel(
    private val cardsRepository: CardsRepository,
    private val paymentManager: PaymentManager,
) : ViewModel() {

    val screenState = initScreenState()

    private val _scannedCard = MutableSharedFlow<String>(replay = 1)
    val scannedCard: Flow<String> = _scannedCard.asSharedFlow()

    fun clearScannedCard() {
        _scannedCard.resetReplayCache()
    }

    fun scanCard() {
        paymentManager.showCardScanner { result ->
            val pan = result.pan
            if (pan.isNotEmpty()) viewModelScope.launch {
                _scannedCard.emit(pan)
            }
        }
    }

    /**
     * Tokenizes the card via Stripe and registers it server-side.
     * [onAdded] fires when the card is persisted so the caller can refresh
     * the shared methods list before dismissing the dialog.
     */
    fun addCard(
        parms: PaymentMethodCreateParams,
        onAdded: suspend () -> Unit,
        onDismiss: () -> Unit
    ) {
        screenState.loading = true
        paymentManager.createToken(
            parms = parms,
            onSuccess = { result ->
                viewModelScope.launch {
                    when (val addResult = cardsRepository.addCard(result.id)) {
                        is ApiStatus.Error -> {
                            screenState.loading = false
                            screenState.showMessage(addResult.msg, onDismiss)
                        }

                        is ApiStatus.Success -> {
                            onAdded()
                            screenState.loading = false
                            screenState.showSuccess(onDismiss)
                        }

                        else -> {}
                    }
                }
            },
            onError = {
                screenState.loading = false
                screenState.showMessage(it.asErrorMessage)
            }
        )
    }
}
