package com.powerly.payment.presentation.balance.show

import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.payment.domain.use_case.GetBalanceItemsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ShowBalanceViewModel(
    private val userRepository: UserRepository,
    private val getBalanceItems: GetBalanceItemsUseCase,
) : ViewModel() {

    val balanceItems = flow {
        emit(ApiStatus.Loading)
        emit(getBalanceItems())
    }

    suspend fun refreshUser() {
        userRepository.getUserDetails()
        delay(500)
    }
}
