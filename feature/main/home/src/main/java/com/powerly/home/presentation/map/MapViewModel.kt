package com.powerly.home.presentation.map

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.ui.util.ActivityResultState
import com.powerly.ui.util.PermissionsState
import com.powerly.core.domain.model.SourcesStatus
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.core.domain.model.location.Target
import com.powerly.core.domain.model.powerly.PowerSource
import com.powerly.core.domain.manager.UserLocationManager
import com.powerly.home.domain.use_case.LocationServicesUseCase
import com.powerly.ui.map.initMapViewState
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class MyMapViewModel(
    private val powerSourceRepository: PowerSourceRepository,
    private val locationManager: UserLocationManager,
    private val locationServicesUseCase: LocationServicesUseCase
) : ViewModel() {

    val userLocation = mutableStateOf<Target?>(null)

    val mapState = initMapViewState(
        initialLocation = userLocation.value,
        zoom = 11f,
        observeLocation = false,
        scrollGesturesEnabled = true,
        zoomGesturesEnabled = true,
        locationEnabled = locationManager.allEnabled
    )

    val selectedPowerSource = mutableStateOf<PowerSource?>(null)

    fun setSelectedPowerSource(powerSource: PowerSource?) {
        selectedPowerSource.value = powerSource
    }

    val nearPowerSources = mutableStateListOf<PowerSource>()
    private val powerSourcesMap = mutableMapOf<String, PowerSource>()


    fun loadPowerSources(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            when (val result = powerSourceRepository.getNearPowerSources(latitude, longitude)) {
                is SourcesStatus.Success -> displayPowerSources(result.sources)
                else -> {}
            }
        }
    }


    suspend fun loadNearPowerSources() {
        val target = userLocation.value ?: return
        Log.v(TAG, "loadNearPowerSources - ($target.latitude, $target.longitude)")
        powerSourcesMap.clear()
        nearPowerSources.clear()
        val result = powerSourceRepository.getNearPowerSources(target.latitude, target.longitude)
        if (result is SourcesStatus.Success) {
            val sources = result.sources
            if (sources.isEmpty()) return
            displayPowerSources(result.sources)
            val nearestSource = nearPowerSources.sortedBy { it.distance() }.firstOrNull {
                it.distance() < 10.0
            } ?: sources.first()
            mapState.moveCamera(nearestSource.location)
        }
    }

    fun displayPowerSources(powerSources: List<PowerSource>) {
        val userLat = userLocation.value?.latitude
        val userLng = userLocation.value?.longitude
        powerSources.forEach {
            it.distance(userLat, userLng)
            powerSourcesMap[it.id] = it
        }
        nearPowerSources.clear()
        nearPowerSources.addAll(powerSourcesMap.values.toList())
    }

    fun requestLocationServices(
        permissionsState: PermissionsState,
        activityResult: ActivityResultState,
        requestManually: Boolean = false,
        onAllowed: () -> Unit
    ) {
        viewModelScope.launch {
            locationServicesUseCase(
                permissionsState = permissionsState,
                activityResult = activityResult,
                requestManually = requestManually,
                onAllowed = onAllowed
            )
        }
    }

    suspend fun initMap() {
        Log.i(TAG, "initMap")
        mapState.locationEnabled.value = true
        val location = locationManager.requestLocation(forceUpdate = true)
        if (location != null) {
            userLocation.value = location
            mapState.initCenterLocation(location)
        }
    }

    fun updateMapLocation() {
        viewModelScope.launch {
            val location = locationManager.requestLocation(forceUpdate = true)
            if (location != null) mapState.moveCamera(location)
        }
    }

    companion object {
        private const val TAG = "MapViewModel"
    }
}
