package com.powerly.payment.balance.withdraw

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.PaymentRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.Wallet
import com.powerly.lib.managers.CountryManager
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.initBasicScreenState
import com.powerly.ui.dialogs.message.MessageState
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch



@KoinViewModel
class WithdrawViewModel (
    private val paymentRepository: PaymentRepository,
    private val appRepository: AppRepository,
    private val countryManager: CountryManager,
    private val storageManager: StorageManager
) : ViewModel() {
    val screenState = initBasicScreenState(LoadingState(), MessageState())
    val wallets = mutableStateListOf<Wallet>()
    val currency: String get() = storageManager.currency

    fun loadWallets() {
        viewModelScope.launch {
            screenState.loading = true
            val it = paymentRepository.walletList()
            when (it) {
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
