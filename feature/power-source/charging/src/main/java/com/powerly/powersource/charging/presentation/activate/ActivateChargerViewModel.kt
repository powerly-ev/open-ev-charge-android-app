package com.powerly.powersource.charging.presentation.activate

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.model.ChargingStatus
import com.powerly.powersource.charging.domain.repository.ChargingRepository
import com.powerly.ui.dialogs.loading.initScreenState
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ActivateChargerViewModel(
    private val chargingRepository: ChargingRepository,
) : ViewModel() {
    val screenState = initScreenState()

    suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ): String? {
        screenState.loading = true
        val result = chargingRepository.startCharging(
            chargePointId = chargePointId,
            quantity = quantity,
            connector = connector
        )
        screenState.loading = false
        when (result) {
            is ChargingStatus.Error -> screenState.showMessage(result.message)
            is ChargingStatus.Success -> return result.session.id
            else -> {}
        }
        return null
    }
}
