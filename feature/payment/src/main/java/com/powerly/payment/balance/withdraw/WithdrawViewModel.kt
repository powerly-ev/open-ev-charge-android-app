package com.powerly.payment.balance.withdraw

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.PaymentRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.Wallet
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.initBasicScreenState
import com.powerly.ui.dialogs.message.MessageState
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class WithdrawViewModel(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val screenState = initBasicScreenState(LoadingState(), MessageState())
    val wallets = mutableStateListOf<Wallet>()
    val currency = userRepository.currencyFlow

    fun loadWallets() {
        viewModelScope.launch {
            screenState.loading = true
            when (val it = paymentRepository.walletList()) {
                is ApiStatus.Error -> screenState.showMessage(it.msg)
                is ApiStatus.Success -> wallets.addAll(it.data)
                else -> {}
            }
        }
    }

    fun walletPayout() = flow {
        emit(ApiStatus.Loading)
        val result = paymentRepository.walletPayout()
        emit(result)
    }

    /**
     * Fetches the user agreements based on country ID.
     *
     * @param type The type of user agreement to fetch.
     */
    suspend fun getUserAgreementLink(type: Int): String? {
        return null
    }
}
