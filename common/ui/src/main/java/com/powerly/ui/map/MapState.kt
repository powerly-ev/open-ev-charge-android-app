package com.powerly.ui.map


import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.powerly.core.model.location.MyAddress
import com.powerly.core.model.location.Target

@Composable
fun rememberMapState(
    initialLocation: Target?,
    lang: String = "en",
    zoom: Float = 14f,
    maxZoom: Float = 21f,
    isGoogle: Boolean = true,
    locationEnabled: Boolean = false,
    filterMarkers: Boolean = false,
    observeLocation: Boolean = false,
    showCenterPin: Boolean = false,
    pinMapCenter: Boolean = false,
    zoomGesturesEnabled: Boolean = false,
    scrollGesturesEnabled: Boolean = false,
) = remember {
    MapViewState(
        location = initialLocation,
        lang = lang,
        zoom = zoom,
        isGoogle = isGoogle,
        maxZoom = maxZoom,
        observeLocation = observeLocation,
        scrollGesturesEnabled = scrollGesturesEnabled,
        zoomGesturesEnabled = zoomGesturesEnabled,
        showCenterPin = showCenterPin,
        pinMapCenter = pinMapCenter,
        locationEnabled = mutableStateOf(locationEnabled),
        filterMarkers = mutableStateOf(filterMarkers)
    )
}

fun initMapViewState(
    initialLocation: Target?,
    lang: String = "en",
    zoom: Float = 14f,
    maxZoom: Float = 21f,
    locationEnabled: Boolean = false,
    filterMarkers: Boolean = false,
    isGoogle: Boolean = true,
    observeLocation: Boolean = false,
    showCenterPin: Boolean = false,
    pinMapCenter: Boolean = false,
    zoomGesturesEnabled: Boolean = false,
    scrollGesturesEnabled: Boolean = false,
) = MapViewState(
    location = initialLocation,
    lang = lang,
    zoom = zoom,
    isGoogle = isGoogle,
    maxZoom = maxZoom,
    observeLocation = observeLocation,
    scrollGesturesEnabled = scrollGesturesEnabled,
    zoomGesturesEnabled = zoomGesturesEnabled,
    showCenterPin = showCenterPin,
    pinMapCenter = pinMapCenter,
    locationEnabled = mutableStateOf(locationEnabled),
    filterMarkers = mutableStateOf(filterMarkers)
)

@Stable
class MapViewState(
    location: Target?,
    val lang: String,
    val zoom: Float,
    val isGoogle: Boolean,
    val maxZoom: Float,
    val observeLocation: Boolean,
    val showCenterPin: Boolean,
    val pinMapCenter: Boolean,
    val zoomGesturesEnabled: Boolean,
    val scrollGesturesEnabled: Boolean,
    val locationEnabled: MutableState<Boolean>,
    val filterMarkers: MutableState<Boolean>,
) {
    internal val centerLocation = mutableStateOf(location)

    var address by mutableStateOf(MyAddress())
        internal set
    var currentCameraZoom by mutableFloatStateOf(zoom)
        internal set

    var moveCamera: (Target) -> Unit = {}
        internal set

    var moveCameraKeepZoom: (Target) -> Unit = {}
        internal set

    var updateMarkers: () -> Unit = {}
        internal set

    val latitude: Double get() = centerLocation.value?.latitude ?: 0.0
    val longitude: Double get() = centerLocation.value?.longitude ?: 0.0
    val hasLocation: Boolean get() = centerLocation.value != null

    fun initCenterLocation(location: Target) {
        centerLocation.value = location
        moveCamera(location)
    }
}