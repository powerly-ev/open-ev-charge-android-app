package com.powerly.core.data.model

import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.core.model.util.Message

sealed class MakersStatus {
    data class Error(val msg: Message) : MakersStatus()
    data class Success(val makers: Map<String, List<VehicleMaker>>) : MakersStatus()
    data object Loading : MakersStatus()
}

sealed class ModelsStatus {
    data class Error(val msg: Message) : ModelsStatus()
    data class Success(val models: List<VehicleModel>) : ModelsStatus()
    data object Loading : ModelsStatus()
}
