package com.powerly.ui.map

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.powerly.core.model.location.Target
import com.powerly.resources.R
import com.powerly.ui.components.ButtonRound
import com.powerly.ui.containers.MyCardRow
import com.powerly.ui.theme.MyColors
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun MapActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Int,
) {
    ButtonRound(
        onClick = onClick,
        icon = icon,
        modifier = Modifier
            .size(55.dp)
            .then(modifier),
        background = MyColors.white,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 3.dp
        ),
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun BoxScope.SectionSearchBox(onClick: () -> Unit) {
    MyCardRow(
        modifier = Modifier
            .padding(16.dp)
            .zIndex(3f)
            .align(Alignment.TopCenter),
        onClick = onClick,
        padding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        ),
        background = Color.White,
        spacing = 16.dp
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = ""
        )
        Text(
            text = stringResource(id = R.string.location_search_address),
            style = MaterialTheme.typography.bodyLarge,
            color = MyColors.subColor
        )
    }
}

@Composable
fun SectionMapPlaceholder(show: () -> Boolean) {
    AnimatedVisibility(
        visible = show(),
        exit = fadeOut()
    ) {
        if (show()) Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .zIndex(10f)
        ) {}
    }
}


/**
 *  A utility object to calculate distance between two latitude and longitude coordinates.
 */
object Distance {
    private const val TAG = "Distance"
    private var oldLatitude = 0.0
    private var oldLongitude = 0.0

    // circle range 50 km
    private const val RANGE = 50

    /**
     *  Determines if the given latitude and longitude are outside the range of the previously
     *  stored latitude and longitude.
     *
     *  @param latitude The latitude to check.
     *  @param longitude The longitude to check.
     *  @return True if the given coordinates are outside the range, false otherwise.
     */
    fun outsideRange(latitude: Double, longitude: Double): Boolean {
        val outside = if (oldLatitude == 0.0 && oldLongitude == 0.0) {
            oldLatitude = latitude
            oldLongitude = longitude
            true
        } else {
            val distance = latLngDistance(latitude, longitude)
            Log.i(TAG, "distance - $distance km")
            if (distance > RANGE) {
                oldLatitude = latitude
                oldLongitude = longitude
                true
            } else false
        }

        return outside
    }

    /**
     * Calculates the distance between two latitude and longitude coordinates using the Haversine formula.
     *
     * @param latitude The latitude of the second coordinate.
     * @param longitude The longitude of the second coordinate.
     * @return The distance between the two coordinates in kilometers.
     */
    private fun latLngDistance(latitude: Double, longitude: Double): Double {
        val r = 6371.0 // Earth's radius in kilometers
        val lat1 = Math.toRadians(latitude)
        val lon1 = Math.toRadians(longitude)
        val lat2 = Math.toRadians(oldLatitude)
        val lon2 = Math.toRadians(oldLongitude)

        val diffLon = lon2 - lon1
        val diffLat = lat2 - lat1

        val a = sin(diffLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(diffLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return String.format(Locale.US, "%.2f", r * c).toDouble()
    }
}

val com.powerly.core.model.location.Target.toLatLng: LatLng get() = LatLng(this.latitude, this.longitude)
val LatLng.toTarget: com.powerly.core.model.location.Target get() = Target(this.latitude, this.longitude)
