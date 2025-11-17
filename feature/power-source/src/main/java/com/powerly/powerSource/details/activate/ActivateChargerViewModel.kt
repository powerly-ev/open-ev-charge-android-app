package com.powerly.powerSource.details.activate

import androidx.lifecycle.ViewModel
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.ui.dialogs.loading.initScreenState
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ActivateChargerViewModel(
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    val screenState = initScreenState()
    suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ): String? {
        screenState.loading = true
        val result = sessionsRepository.startCharging(
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