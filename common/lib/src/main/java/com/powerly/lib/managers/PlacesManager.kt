package com.powerly.lib.managers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.powerly.core.model.location.MyAddress
import com.powerly.core.model.location.Target
import com.powerly.core.network.DeviceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.util.Locale

/**
 * A manager class for interacting with the Google Places API.
 * Provides functionality for autocomplete place queries and fetching place details.
 */
@Single
class PlacesManager(
    private val context: Context,
    private val placesProvider: PlacesProvider,
    deviceHelper: DeviceHelper,
) {
    private var placesClient: PlacesClient

    companion object {
        private const val TAG = "PlacesManager"

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: PlacesManager
    }

    /**
     * Initializes the Places API client.
     */

    init {
        // Create a new Places client instance.
        Places.initializeWithNewPlacesApiEnabled(context, deviceHelper.googlePlacesApiKey)
        placesClient = Places.createClient(context)
        instance = this
        placesProvider.initMap()
    }

    /**
     * Performs autocomplete queries, returning a list of places.
     *
     * @param query The search query.
     * @param onSuccess Called when the query successfully completes, providing a list of places.
     * @param onError Optional callback invoked if an error occurs, providing an error message.
     */
    fun autoCompletePlaces(
        query: String,
        onSuccess: (List<PlaceItem>) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        Log.v(TAG, "autoCompletePlaces - $query")

        // Create a new token for the autocomplete session.
        val token = AutocompleteSessionToken.newInstance()
        val autocompletePlacesRequest = FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        // Perform autocomplete query
        placesClient.findAutocompletePredictions(autocompletePlacesRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                Log.i(TAG, "predictions ---> ${predictions.size}")
                // Convert predictions to PlaceItem objects
                val places = getPlaces(predictions)
                onSuccess(places)
            }
            .addOnFailureListener { exception ->
                val message = exception.message.orEmpty()
                Log.e(TAG, message)
                onError?.invoke(message)
                exception.printStackTrace()
            }
    }

    /**
     * Converts a list of AutocompletePredictions to a list of PlaceItem objects.
     *
     * @param predictions The list of AutocompletePrediction objects.
     * @return A list of PlaceItem objects.
     */
    private fun getPlaces(predictions: List<AutocompletePrediction>): List<PlaceItem> {
        val places = mutableListOf<PlaceItem>()
        predictions.forEach { prediction ->
            val place = PlaceItem(
                placeId = prediction.placeId,
                primary = prediction.getPrimaryText(StyleSpan(Typeface.BOLD)).toString(),
                secondary = prediction.getSecondaryText(StyleSpan(Typeface.NORMAL)).toString(),
                description = prediction.getFullText(StyleSpan(Typeface.NORMAL)).toString()
            )
            places.add(place)
        }
        return places
    }

    /**
     * Fetches the location (latitude and longitude) of a place given its place ID.
     *
     * @param placeId The ID of the place.
     * @param onFetch Callback invoked when the place location is fetched, providing a Target object.
     */
    fun fetchPlaceLocation(
        placeId: String,
        onFetch: (Target) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        Log.i(TAG, "fetchPlaceLocation - $placeId")

        // Specify the fields to be retrieved for the place (in this case, only LAT_LNG).
        val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG)

        // Build a FetchPlaceRequest using the Place ID and specified fields.
        val request: FetchPlaceRequest = FetchPlaceRequest
            .builder(placeId, placeFields)
            .build()

        // Use the PlacesClient to asynchronously fetch place details.
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            // If the request is successful, extract the latitude and longitude from the response.
            response.place.location?.let {
                // Create a Target object using the retrieved latitude and longitude.
                val target = Target(it.latitude, it.longitude)
                // Invoke the provided callback function with the Target object.
                onFetch(target)
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
            onError?.invoke(e.message.orEmpty())
        }
    }


    /**
     * Detects an address based on the provided latitude and longitude.
     *
     * This function uses the [PlacesProvider] to retrieve an address for the given coordinates.
     * The address detection process is performed in a background thread using [Dispatchers.IO].
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param lang The language code for the address results (e.g., "en", "es").
     *             Defaults to the device's default language.
     */
    suspend fun detectAddress(
        latitude: Double,
        longitude: Double,
        lang: String = Locale.getDefault().language
    ): MyAddress? = withContext(Dispatchers.IO) {
        placesProvider.detectAddress(
            latitude = latitude,
            longitude = longitude,
            lang = lang
        )
    }
}

/**
 * Represents a place item returned from an autocomplete query.
 *
 * @property placeId The ID of the place.
 * @property primary The primary text describing the place.
 * @property secondary The secondary text describing the place.
 * @property description The full description of the place.
 */
data class PlaceItem(
    val placeId: String = "",
    val primary: String = "",
    val secondary: String = "",
    val description: String = ""
)