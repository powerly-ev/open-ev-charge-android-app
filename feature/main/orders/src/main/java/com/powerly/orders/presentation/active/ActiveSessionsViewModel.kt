package com.powerly.orders.presentation.active

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.Session
import com.powerly.orders.domain.repository.SessionsRepository
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ActiveSessionsViewModel(
    private val sessionsRepository: SessionsRepository
) : ViewModel() {

    private var pendingSessionId: String = ""

    suspend fun stopCharging(session: Session?): ApiStatus<Session>? {
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