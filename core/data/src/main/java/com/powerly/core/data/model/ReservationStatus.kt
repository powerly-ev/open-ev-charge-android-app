package com.powerly.core.data.model

import com.powerly.core.model.powerly.Session
import com.powerly.core.model.util.Message

sealed class ReservationStatus {
    data class Error(val msg: Message) : ReservationStatus()
    data object Cancel : ReservationStatus()
    data class Success(val reservation: Session) : ReservationStatus()
    data object Loading : ReservationStatus()
}