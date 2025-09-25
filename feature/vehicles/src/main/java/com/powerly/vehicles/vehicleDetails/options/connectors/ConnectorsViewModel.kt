package com.powerly.vehicles.vehicleDetails.options.connectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.powerly.Connector
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



@KoinViewModel
class ConnectorsViewModel (
    private val powerSourceRepository: PowerSourceRepository,
) : ViewModel() {

    private val _connectors = MutableStateFlow<ApiStatus<List<Connector>>>(ApiStatus.Loading)
    val connectors: StateFlow<ApiStatus<List<Connector>>> = _connectors.asStateFlow()

    init {
        getConnectors()
    }

    fun getConnectors() {
        // fetch connectors once to avoid unnecessary calls
        if (_connectors.value !is ApiStatus.Success) {
            viewModelScope.launch {
                _connectors.emit(ApiStatus.Loading)
                _connectors.value = powerSourceRepository.connectors()
            }
        }
    }
}