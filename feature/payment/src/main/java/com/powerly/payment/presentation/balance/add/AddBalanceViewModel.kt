package com.powerly.payment.presentation.balance.add

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.repository.UserRepository
import com.powerly.payment.domain.model.StripCard
import com.powerly.core.domain.model.Message
import com.powerly.payment.PaymentManager
import com.powerly.payment.domain.model.BalanceRefillStatus
import com.powerly.payment.domain.repository.BalanceRepository
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.initAlertDialogState
import com.stripe.android.payments.paymentlauncher.PaymentResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.annotation.KoinViewModel
import kotlin.coroutines.resume

@KoinViewModel
class AddBalanceViewModel(
    private val userRepository: UserRepository,
    private val balanceRepository: BalanceRepository,
    private val paymentManager: PaymentManager,
) : ViewModel() {

    val paymentFailureDialog = initAlertDialogState()

    suspend fun refillBalance(offerId: Int, stripCard: StripCard): Boolean {
        val result = balanceRepository.refillBalance(
            offerId = offerId,
            paymentMethodId = stripCard.id
        )
        return when (result) {
            is BalanceRefillStatus.Error -> {
                paymentFailureDialog.show(result.msg.value)
                false
            }

            is BalanceRefillStatus.Authenticate -> {
                val authenticated = authenticatePayment(
                    redirectUrl = result.redirectUrl,
                    message = result.message
                )
                if (authenticated) updateBalanceValue(result.balance)
                authenticated
            }

            is BalanceRefillStatus.Success -> {
                updateBalanceValue(result.balance)
                true
            }

            else -> false
        }
    }

    private fun updateBalanceValue(newBalance: Double) {
        viewModelScope.launch {
            userRepository.updateLocallBalance(newBalance)
        }
    }

    private suspend fun authenticatePayment(
        redirectUrl: String,
        message: Message
    ): Boolean = suspendCancellableCoroutine { continuation ->
        val clientSecret = redirectUrl.toUri()
            .getQueryParameter("payment_intent_client_secret")
            .orEmpty()
        Log.v(TAG, " clientSecret - $clientSecret")
        paymentManager.handleNextActionForPayment(
            clientSecret = clientSecret,
            onResult = {
                when (it) {
                    is PaymentResult.Canceled -> {
                        val msg = paymentManager.appContext.getString(R.string.payment_failed)
                        paymentFailureDialog.show(msg)
                        continuation.resume(false)
                    }

                    is PaymentResult.Failed -> {
                        paymentFailureDialog.show(it.throwable.message.orEmpty())
                        continuation.resume(false)
                    }

                    is PaymentResult.Completed -> {
                        continuation.resume(true)
                    }
                }
            }
        )
    }

    companion object {
        private const val TAG = "AddBalanceViewModel"
    }
}
