package com.powerly.home.map

import com.powerly.core.model.location.Target
import com.powerly.core.model.powerly.PowerSource

internal sealed class MapEvents() {
    data object OnLocation : MapEvents()
    data object OnSearch : MapEvents()
    data object OnBack : MapEvents()
    data object OpenBalance : MapEvents()
    data object OpenSupport : MapEvents()
    data class NearPowerSources(val target: Target) : MapEvents()
    data class OpenPowerSource(val powerSource: PowerSource) : MapEvents()
    data class SelectPowerSource(
        val powerSource: PowerSource,
        val selectByScroll: Boolean
    ) : MapEvents()

}
