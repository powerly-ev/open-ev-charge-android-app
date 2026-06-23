package com.powerly.payment.presentation

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.domain.model.ApiStatus
import com.powerly.payment.domain.model.BalanceItem
import com.powerly.payment.domain.model.StripCard
import com.powerly.payment.domain.repository.CardsRepository
import com.powerly.ui.dialogs.loading.initScreenState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

/**
 * Holds state shared across every screen in the Payment nav graph:
 * the list of payment methods, the chosen default, and the balance offer
 * the user is currently topping up. Resolved via [com.powerly.ui.navigation.sharedGraphViewModel].
 */
@KoinViewModel
class PaymentViewModel(
    private val userRepository: UserRepository,
    private val cardsRepository: CardsRepository,
) : ViewModel() {

    val screenState = initScreenState()
    val paymentMethods = mutableStateListOf<StripCard>()
    val defaultPaymentMethod = mutableStateOf<StripCard?>(null)
    val balanceItem = mutableStateOf(BalanceItem())
    val userBalance = mutableDoubleStateOf(0.0)
    val userCurrency = mutableStateOf("")

    init {
        viewModelScope.launch {
            userRepository.userFlow.filterNotNull().collect {
                userBalance.doubleValue = it.balance
                userCurrency.value = it.currency
            }
        }

        // Clear cached methods on logout so the next user doesn't see stale cards.
        viewModelScope.launch {
            userRepository.logoutEvent.collect {
                paymentMethods.clear()
                defaultPaymentMethod.value = null
            }
        }
    }

    fun setBalanceItem(item: BalanceItem) {
        balanceItem.value = item
    }

    suspend fun loadPaymentMethods(forceUpdate: Boolean = false) {
        if (paymentMethods.isEmpty() || forceUpdate) {
            when (val it = cardsRepository.cardList()) {
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
        when (val result = cardsRepository.setDefaultCard(card.id)) {
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

    suspend fun deleteCard(cardId: String): Boolean {
        when (val result = cardsRepository.deleteCard(cardId)) {
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            is ApiStatus.Success -> {
                loadPaymentMethods(forceUpdate = true)
                return true
            }

            else -> {}
        }
        return false
    }
}
