package com.powerly.lib.managers

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.powerly.core.database.LocalDataSource
import com.powerly.core.model.user.User
import com.powerly.core.network.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.Locale
import java.util.UUID

@Single
class StorageManager(
    private val context: Context,
    private val localDataSource: LocalDataSource,
    private val retrofitClient: RetrofitClient,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) {
    private val gson: Gson = GsonBuilder()
        // Register a custom TypeAdapter for Int type to handle null and empty json values
        .registerTypeAdapter(Int::class.javaObjectType, IntTypeAdapter())
        .create()


    private val preferences = context.getSharedPreferences(
        USER_PREFERENCES, Context.MODE_PRIVATE
    )
    private val appPreferences = context.getSharedPreferences(
        APP_PREFERENCES, Context.MODE_PRIVATE
    )

    init {
        retrofitClient.initClients(userToken)
    }

    var messagingToken: String
        get() = preferences.getString(FCM_TOKEN, "").orEmpty()
        set(token) {
            preferences.edit { putString(FCM_TOKEN, token) }
        }

    var userToken: String
        get() = preferences.getString(USER_TOKEN, "").orEmpty()
        set(token) {
            retrofitClient.initClients(token)
            preferences.edit { putString(USER_TOKEN, token) }
        }

    var currency: String
        get() = userDetails?.currency ?: preferences.getString(CURRENCY, "").orEmpty()
        set(currency) {
            preferences.edit { putString(CURRENCY, currency) }
        }

    var currentLanguage: String
        get() = appPreferences.getString(LANGUAGE, Locale.getDefault().language).orEmpty()
        set(language) {
            appPreferences.edit { putString(LANGUAGE, language) }
        }

    fun imei(): String {
        if (_uniqueID == null) {
            _uniqueID = appPreferences.getString(UNIQUE_ID, null)
            if (_uniqueID == null) {
                _uniqueID = UUID.randomUUID().toString()
                appPreferences.edit { putString(UNIQUE_ID, _uniqueID) }
            }
        }
        return _uniqueID.orEmpty()
    }

    val isLoggedIn: Boolean get() = userToken.isEmpty().not()

    val countryId: Int? get() = userDetails?.countryId
    val userId: Int? get() = userDetails?.id

    var userDetails: User?
        get() = _user ?: getPref(USER)
        set(data) {
            _user = data
            setPref(USER, data)
        }

    var showRegisterNotification: Boolean
        get() = isLoggedIn.not() && preferences.getString(REGISTER_NOTIFICATION, "0") == "0"
        set(show) {
            preferences.edit { putString(REGISTER_NOTIFICATION, if (show) "0" else "1") }
        }

    var showOnBoarding: Boolean
        get() = preferences.getBoolean(ON_BOARDING, true)
        set(show) {
            preferences.edit { putBoolean(ON_BOARDING, show) }
        }

    var pinUserCurrency: Boolean
        get() = preferences.getBoolean(CURRENCY_STATIC, false)
        set(static) {
            preferences.edit { putBoolean(CURRENCY_STATIC, static) }
        }

    suspend fun logOutAll() {
        retrofitClient.initClients("")
        _user = null
        userDetails = null
        preferences.edit { clear() }
        withContext(ioDispatcher) {
            localDataSource.clearCountry()
        }
    }

    suspend fun clearCountry() = withContext(ioDispatcher) {
        localDataSource.clearCountry()
    }

    fun saveLogin(user: User) {
        val token = user.accessToken ?: return
        userToken = token
        userDetails = user
        currency = user.currency
    }


    private inline fun <reified T> getPref(key: String): T? {
        val type = object : TypeToken<T>() {}.type
        val data = preferences.getString(key, null)
        return if (!data.isNullOrEmpty()) gson.fromJson(data, type) else null
    }

    private fun setPref(key: String, data: Any?) {
        val userJson = gson.toJson(data)
        preferences.edit { putString(key, userJson) }
    }


    companion object {
        private const val USER_PREFERENCES = "UserStorage"
        private const val APP_PREFERENCES = "AppStorage"
        private const val UNIQUE_ID = "PREF_UNIQUE_ID"
        private const val USER = "USER_DETAILS"
        private const val USER_TOKEN = "TOKEN"
        private const val FCM_TOKEN = "fcm_token"
        private const val CURRENCY = "CURRENCY"
        private const val CURRENCY_STATIC = "static_currency"
        private const val LANGUAGE = "language"
        private const val REGISTER_NOTIFICATION = "notification"
        private const val ON_BOARDING = "on_boarding"

        private var _user: User? = null
        private var _uniqueID: String? = null
    }
}


/**
 * Custom TypeAdapter for the Int?type that handles empty strings as null values.
 */
class IntTypeAdapter : TypeAdapter<Int?>() {

    /*** Reads a JSON value and converts it to an Int?.
     *
     * - If the JSON value is null, returns null.
     * - If the JSON value is an empty string, returns null.
     * - Otherwise, attempts to parse the JSON value as an Int and returns the result.
     *   If the parsing fails, returns null.
     */

    override fun read(reader: JsonReader): Int? {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val stringValue: String = reader.nextString()
        if (stringValue.isEmpty()) return null
        return try {
            stringValue.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Writes an Int? value to a JSON writer.
     *
     * - If the value is null, writes a null JSON value.
     * - Otherwise, writes the value as a JSON number.
     */
    override fun write(writer: JsonWriter, value: Int?) {
        if (value == null) writer.nullValue()
        else writer.value(value)
    }
}