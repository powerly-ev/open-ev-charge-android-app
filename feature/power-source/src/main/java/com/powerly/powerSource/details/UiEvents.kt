package com.powerly.powerSource.details

import com.powerly.core.model.location.Target

sealed class SourceEvents() {
    data object Close : SourceEvents()
    data object Reviews : SourceEvents()
    data object Media : SourceEvents()
    data object HowToCharge : SourceEvents()
    data object Balance : SourceEvents()
    data object Charge : SourceEvents()
    data class CAll(val contact: String?) : SourceEvents()
    data class DriveToStation(val target: Target) : SourceEvents()

}
