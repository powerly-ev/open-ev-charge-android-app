package com.powerly.ui.map

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.powerly.core.model.location.Target
import com.powerly.lib.managers.PlacesManager
import com.powerly.resources.R
import com.powerly.ui.map.Distance
import com.powerly.ui.map.MapViewState
import com.powerly.ui.map.SectionSearchBox
import com.powerly.ui.map.rememberMapState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MyMapScreen"


@Preview
@Composable
private fun MapScreenPreview() {
    MyMapScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        mapState = rememberMapState(
            initialLocation = Target(25.0, 25.0),
            showCenterPin = true
        ),
        onSearchClick = {

        },
        floatingContent = {

        }
    )
}

@Composable
fun MyMapScreen(
    modifier: Modifier,
    mapState: MapViewState,
    onMapLoaded: (() -> Unit)? = null,
    onMapClick: (() -> Unit)? = null,
    onMapLongClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    onMapDrag: (() -> Unit)? = null,
    onCameraLongMove: ((Double, Double) -> Unit)? = null,
    floatingContent: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable @GoogleMapComposable () -> Unit = {},
) {
    Box(modifier = modifier) {

        if (onSearchClick != null) SectionSearchBox {
            onSearchClick()
        }
        if (mapState.showCenterPin) Image(
            painter = painterResource(id = R.drawable.ic_location_pin),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(2f)
        )

        floatingContent?.let {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .zIndex(3f)
            ) {
                it()
            }
        }

        MyMapView(
            mapState = mapState,
            content = content,
            onCameraLongMove = onCameraLongMove,
            onMapDrag = onMapDrag,
            onMapLongClick = onMapLongClick,
            onMapClick = onMapClick,
            onMapLoaded = onMapLoaded
        )
    }
}

@Composable
fun MyMapView(
    mapState: MapViewState,
    onMapLoaded: (() -> Unit)? = null,
    onMapClick: (() -> Unit)? = null,
    onMapLongClick: (() -> Unit)? = null,
    onMapDrag: (() -> Unit)? = null,
    onCameraLongMove: ((Double, Double) -> Unit)? = null,
    content: @Composable @GoogleMapComposable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var centerLocation by remember { mapState.centerLocation }
    if (centerLocation == null) return

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            centerLocation!!.toLatLng, mapState.zoom
        )
    }
    val context = LocalContext.current
    val mapProperties = remember {
        MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.map_style
            ),
            maxZoomPreference = mapState.maxZoom
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        onMapClick = { onMapClick?.invoke() },
        onMapLongClick = { onMapLongClick?.invoke() },
        uiSettings = MapUiSettings(
            mapToolbarEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = mapState.zoomGesturesEnabled,
            scrollGesturesEnabled = mapState.scrollGesturesEnabled
        ),
        onMapLoaded = onMapLoaded,
        content = content
    )

    /**
     * Animates the camera to the given target and zoom level.
     *
     * @param target The target location to move the camera to.
     * @param zoom The zoom level to set the camera to.
     */
    fun moveCamera(target: Target, zoom: Float) {
        centerLocation = target
        try {
            coroutineScope.launch {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(target.toLatLng, zoom, 0f, 0f)
                    ),
                    durationMs = 1000
                )
            }
        }
        // handles CameraUpdateFactory is not initialized exception
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateAddress(target: Target) {
        coroutineScope.launch {
            PlacesManager.instance.detectAddress(
                lang = mapState.lang,
                latitude = target.latitude,
                longitude = target.longitude
            )?.let { myAddress ->
                mapState.address = myAddress
                centerLocation = cameraPositionState.position.target.toTarget
            }
        }
    }

    LaunchedEffect(Unit) {
        if (mapState.pinMapCenter) return@LaunchedEffect
        val currentLocation = cameraPositionState.position.target.toTarget
        if (
            centerLocation != currentLocation ||
            mapState.currentCameraZoom != cameraPositionState.position.zoom
        ) {
            Log.v(TAG, "update-map-center-location")
            cameraPositionState.move(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        centerLocation!!.toLatLng,
                        mapState.currentCameraZoom, 0f, 0f
                    )
                )
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        // update map camera location with default zoom value
        mapState.moveCamera = {
            Log.i(TAG, "moveCamera")
            mapState.currentCameraZoom = mapState.zoom
            moveCamera(it, mapState.zoom)
        }
        //update map camera location with current zoom value
        mapState.moveCameraKeepZoom = {
            Log.i(TAG, "moveCameraKeepZoom")
            moveCamera(it, cameraPositionState.position.zoom)
        }
    }

    if (mapState.observeLocation) {
        LaunchedEffect(Unit) {
            updateAddress(centerLocation!!)
        }
    }

    LaunchedEffect(
        key1 = cameraPositionState.position,
        key2 = cameraPositionState.isMoving
    ) {
        if (cameraPositionState.isMoving.not()) {
            val lat = cameraPositionState.position.target.latitude
            val lng = cameraPositionState.position.target.longitude
            if (mapState.observeLocation) updateAddress(Target(lat, lng))
            if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
                delay(1000)
                Log.i(TAG, "onMapDrag")
                mapState.currentCameraZoom = cameraPositionState.position.zoom
                centerLocation = cameraPositionState.position.target.toTarget
                onMapDrag?.invoke()
                // call onCameraLongMove function only when camera move by dragging
                // and if camera moved to a distance Distance.RANGE > 50 km
                if (onCameraLongMove != null && Distance.outsideRange(lat, lng)) {
                    Log.i(TAG, "onCameraLongMove")
                    onCameraLongMove(lat, lng)
                }
            }
        }
    }
}
