package com.powerly.orders

import androidx.lifecycle.ViewModel
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.core.model.powerly.Session
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class ChargerViewModel (
    private val sessionsRepository: SessionsRepository
) : ViewModel() {

    private var pendingSessionId: String = ""

    suspend fun stopCharging(session: Session?): ChargingStatus? {
        // to prevent calling stop charging multiple times..
        if (session == null || session.id == pendingSessionId) return null

        pendingSessionId = session.id
        val result = sessionsRepository.stopCharging(
            chargePointId = session.chargePointId,
            orderId = session.id,
            connector = session.connectorNumber
        )
        pendingSessionId = ""
        return result
    }
}