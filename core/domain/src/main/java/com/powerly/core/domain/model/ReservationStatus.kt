package com.powerly.core.domain.model

import com.powerly.core.model.powerly.Session
import com.powerly.core.domain.model.Message

sealed class ReservationStatus {
    data class Error(val msg: Message) : ReservationStatus()
    data object Cancel : ReservationStatus()
    data class Success(val reservation: Session) : ReservationStatus()
    data object Loading : ReservationStatus()
}
