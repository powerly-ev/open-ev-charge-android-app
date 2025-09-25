package com.powerly.payment.balance

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.powerly.core.analytics.EVENTS
import com.powerly.core.analytics.EventsManager
import com.powerly.core.data.model.BalanceRefillStatus
import com.powerly.core.data.repositories.PaymentRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.core.model.payment.PaymentRedirect
import com.powerly.core.model.payment.StripCard
import com.powerly.core.model.util.Message
import com.powerly.lib.managers.CountryManager
import com.powerly.lib.managers.StorageManager
import com.powerly.payment.PaymentManager
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.initAlertDialogState
import com.stripe.android.payments.paymentlauncher.PaymentResult
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@KoinViewModel
class BalanceViewModel (
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val eventsManager: EventsManager,
    private val paymentManager: PaymentManager,
    private val countryManager: CountryManager,
    private val storageManager: StorageManager,
) : ViewModel() {
    val paymentFailureDialog = initAlertDialogState()
    val balanceItem = mutableStateOf(BalanceItem())
    val userBalance: Double get() = storageManager.userDetails?.balance ?: 0.0
    val userCurrency: String get() = storageManager.userDetails?.currency.orEmpty()

    fun setBalanceItem(item: BalanceItem) {
        this.balanceItem.value = item
    }

    val balanceItems: Flow<ApiStatus<List<BalanceItem>>> = flow {
        emit(ApiStatus.Loading)
        val countryId = countryManager.detectCountry()?.id
        val result = paymentRepository.getBalanceItems(countryId)
        emit(result)
    }

    suspend fun refillBalance(stripCard: StripCard): Boolean {
        log(EVENTS.BALANCE_REFILL)
        val it = paymentRepository.refillBalance(
            offerId = balanceItem.value.id,
            paymentMethodId = stripCard.id
        )
        when (it) {
            is BalanceRefillStatus.Error -> {
                paymentFailureDialog.show(it.msg.msg)
                return false
            }

            is BalanceRefillStatus.Authenticate -> {
                val authenticated = authenticatePayment(
                    paymentRedirect = it.redirect,
                    message = it.message
                )
                return authenticated
            }

            is BalanceRefillStatus.Success -> {
                return true
            }

            else -> {
                return false
            }
        }
    }

    private suspend fun authenticatePayment(
        paymentRedirect: PaymentRedirect,
        message: Message
    ): Boolean = suspendCoroutine { continuation ->
        val uri = paymentRedirect.url.toUri()
        //val stripeAccountId = uri.getQueryParameter("merchant").orEmpty()
        //val paymentIntent = uri.getQueryParameter("payment_intent").orEmpty()
        val clientSecret = uri.getQueryParameter("payment_intent_client_secret").orEmpty()
        //val publishableKey = uri.getQueryParameter("publishable_key").orEmpty()
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

    suspend fun updateUserDetails() {
        val result = userRepository.getUserDetails()
        if (result is ApiStatus.Success) {
            storageManager.userDetails = result.data
            delay(500)
        }
    }


    fun log(event: String) {
        eventsManager.log(event)
    }

    companion object {
        private const val TAG = "BalanceViewModel"
    }
}