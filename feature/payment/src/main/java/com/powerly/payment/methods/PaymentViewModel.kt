package com.powerly.payment.methods

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.PaymentRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.StripCard
import com.powerly.core.model.util.asErrorMessage
import com.powerly.payment.PaymentManager
import com.powerly.ui.dialogs.loading.initScreenState
import com.stripe.android.model.PaymentMethodCreateParams
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


@KoinViewModel
class PaymentViewModel (
    private val paymentRepository: PaymentRepository,
    private val paymentManager: PaymentManager
) : ViewModel() {

    val screenState = initScreenState()
    val paymentMethods = mutableStateListOf<StripCard>()
    val defaultPaymentMethod = mutableStateOf<StripCard?>(null)

    suspend fun loadPaymentMethods(forceUpdate: Boolean = false) {
        if (paymentMethods.isEmpty() || forceUpdate) {
            val it = paymentRepository.cardList()
            when (it) {
                is ApiStatus.Success -> {
                    paymentMethods.clear()
                    paymentMethods.addAll(it.data)
                    defaultPaymentMethod.value =
                        paymentMethods.firstOrNull { c -> c.default }
                            ?: paymentMethods.firstOrNull()
                }

                else -> {}
            }
        }
    }

    suspend fun setDefaultCard(card: StripCard): Boolean {
        if (card.default) return true
        val result = paymentRepository.setDefaultCard(card.id)
        when (result) {
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            is ApiStatus.Success -> {
                loadPaymentMethods(forceUpdate = true)
                defaultPaymentMethod.value = card
                return true
            }

            else -> {}
        }
        return false
    }

    fun addCard(
        parms: PaymentMethodCreateParams,
        onDismiss: () -> Unit
    ) {
        screenState.loading = true
        paymentManager.createToken(
            parms = parms,
            onSuccess = { result ->
                viewModelScope.launch {
                    val result = paymentRepository.addCard(result.id)
                    when (result) {
                        is ApiStatus.Error -> {
                            screenState.loading = false
                            screenState.showMessage(result.msg, onDismiss)
                        }

                        is ApiStatus.Success -> {
                            loadPaymentMethods(forceUpdate = true)
                            screenState.loading = false
                            screenState.showSuccess(onDismiss)
                        }

                        else -> {}
                    }
                }
            },
            onError = {
                screenState.loading = false
                screenState.showMessage(it.asErrorMessage) {
                    onDismiss()
                }
            }
        )
    }

    suspend fun deleteCard(cardId: String): Boolean {
        val result = paymentRepository.deleteCard(cardId)
        when (result) {
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            is ApiStatus.Success -> {
                loadPaymentMethods(forceUpdate = true)
                return true
            }

            else -> {}
        }
        return false
    }

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

    companion object {
        private const val TAG = "PaymentViewModel"
    }
}