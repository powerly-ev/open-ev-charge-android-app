package com.powerly.ui.map

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.PowerSource
import com.powerly.resources.R
import com.powerly.ui.components.MyIcon
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.theme.AppTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MyMarkers"

@Composable
fun PowerSourceMarker(
    powerSource: PowerSource,
    selected: () -> Boolean,
    zoom: () -> Float = { 12f },
    showRating: Boolean = true,
    animated: Boolean = false,
    onClick: () -> Unit
) {
    val isSelected = selected()
    val coroutineScope = rememberCoroutineScope()
    var placement by remember { mutableFloatStateOf(0.001f) }
    val position = remember { powerSource.location.toLatLng }
    val selectedMarkerState = rememberMarkerState(
        key = "${powerSource.id}-${powerSource.location.latitude}",
        position = position
    )

    if (animated && isSelected) {
        val z = zoom()
        LaunchedEffect(z) {
            placement = when {
                z >= 18f -> 0.0001f
                z >= 16f -> 0.0002f
                z >= 14f -> 0.001f
                z >= 10f -> 0.002f
                z >= 8f -> 0.03f
                z >= 5f -> 0.05f
                else -> 0.1f
            }
            Log.i(TAG, "zoom - $z | placement = $placement")
        }
        LaunchedEffect(selected) {
            repeat(1000) {
                selectedMarkerState.position =
                    LatLng(position.latitude + placement, position.longitude)
                delay(1000)
                selectedMarkerState.position = LatLng(position.latitude, position.longitude)
                delay(1000)
            }
        }
    }

    if (isSelected) MarkerComposable(
        state = selectedMarkerState,
        zIndex = if (powerSource.isExternal) 2f else 3f,
        onClick = {
            coroutineScope.launch { onClick() }
            false
        },
        content = {
            PowerSourceMarkerView(
                powerSource = powerSource,
                selected = true,
                showRating = showRating
            )
        }
    ) else MarkerComposable(
        state = rememberMarkerState(
            key = "${powerSource.id}-${powerSource.location.latitude}",
            position = powerSource.location.toLatLng
        ),
        zIndex = if (powerSource.isExternal) 1f else 3f,
        onClick = {
            coroutineScope.launch { onClick() }
            false
        },
        content = {
            PowerSourceMarkerView(
                powerSource = powerSource,
                selected = false,
                showRating = showRating
            )
        }
    )
}


@Preview
@Composable
private fun PowerSourceMarkerPreview() {
    val ps1 = PowerSource(id = "1", rating = 5.0, isExternal = false)
    val ps2 = PowerSource(id = "2", rating = 0.0, isExternal = false)
    val ps3 = PowerSource(id = "3", isExternal = true)

    AppTheme {
        MyRow(verticalAlignment = Alignment.CenterVertically) {
            PowerSourceMarkerView(ps1, selected = true)
            PowerSourceMarkerView(ps2, selected = false)
            PowerSourceMarkerView(ps3, selected = false)
        }
    }
}

private const val markerWidth = 35
private const val markerHeight = 50

@Composable
private fun PowerSourceMarkerView(
    powerSource: PowerSource,
    selected: Boolean,
    showRating: Boolean = true
) {
    val scale = if (selected) 1.5f else 1f
    val infiniteTransition = rememberInfiniteTransition(
        label = powerSource.id
    )
    val position by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            tween(500),
            RepeatMode.Restart
        ),
        label = powerSource.id
    )

    MyColumn(
        spacing = 0.dp,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.Transparent)
            .offset(y = position.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        if (powerSource.isExternal.not() && showRating) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
            ) {
                MyRow(
                    spacing = 4.dp,
                    modifier = Modifier.padding(4.dp)
                ) {
                    MyIcon(
                        icon = R.drawable.ic_baseline_star_24,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (powerSource.rating > 0) powerSource.rating.toString()
                        else stringResource(R.string.station_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.station_internal),
            modifier = Modifier.size(
                (markerWidth * scale).dp,
                (markerHeight * scale).dp
            ),
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )
    }
}

