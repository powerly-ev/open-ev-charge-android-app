package com.powerly.core.data.model

import com.powerly.core.model.powerly.Session
import com.powerly.core.model.util.Message

sealed class ChargingStatus {
    data class Error(val msg: Message) : ChargingStatus()
    data class Success(val session: Session) : ChargingStatus()
    data class Stop(val session: Session) : ChargingStatus()
    data object Loading : ChargingStatus()
}
