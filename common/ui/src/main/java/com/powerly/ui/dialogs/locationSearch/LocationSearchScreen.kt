package com.powerly.ui.dialogs.locationSearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.powerly.core.model.location.Target
import com.powerly.lib.managers.PlaceItem
import com.powerly.lib.managers.PlacesManager


private const val TAG = " LocationSearchScreen"

@Composable
internal fun LocationSearchScreen(
    onSelectPlace: (Target) -> Unit,
    onClose: () -> Unit
) {
    val placesManager = remember { PlacesManager.instance }
    val places = remember { mutableStateListOf<PlaceItem>() }

    LocationSearchScreenContent(
        places = { places },
        onQueryChanges = { query ->
            placesManager.autoCompletePlaces(
                query = query,
                onSuccess = {
                    places.clear()
                    places.addAll(it)
                }, onError = {
                    places.clear()
                }
            )
        },
        onSelectPlace = {
            placesManager.fetchPlaceLocation(
                placeId = it.placeId,
                onFetch = onSelectPlace
            )
        },
        onClose = onClose
    )
}

