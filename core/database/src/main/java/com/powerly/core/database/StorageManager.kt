package com.powerly.core.database

import android.content.Context
import androidx.core.content.edit
import com.powerly.core.model.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.Locale
import java.util.UUID

@Single
class StorageManager(
    private val context: Context,
    private val localDataSource: LocalDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: Flow<User?> = _userFlow.asStateFlow()

    private var _userToken: String? = null
    private var _language: String? = null

    private val preferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
    private val appPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    init {
        _userFlow.value = userDetails
        _userToken = userToken
        _language = currentLanguage
    }

    var messagingToken: String
        get() = preferences.getString(FCM_TOKEN, "").orEmpty()
        set(token) {
            preferences.edit { putString(FCM_TOKEN, token) }
        }

    var userToken: String
        get() = _userToken ?: preferences.getString(USER_TOKEN, "").orEmpty()
        set(token) {
            _userToken = token
            preferences.edit { putString(USER_TOKEN, token) }
        }

    var currency: String
        get() = userDetails?.currency ?: preferences.getString(CURRENCY, "").orEmpty()
        set(currency) {
            preferences.edit { putString(CURRENCY, currency) }
        }

    var currentLanguage: String
        get() = _language ?: appPreferences.getString(LANGUAGE, Locale.getDefault().language)
            .orEmpty()
        set(language) {
            _language = language
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

    val isLoggedIn: Boolean get() = userToken.isNotEmpty()

    val countryId: Int? get() = userDetails?.countryId

    var userDetails: User?
        get() = _userFlow.value ?: getUser()
        set(data) {
            _userFlow.value = data
            storeUser(data)
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

    suspend fun clearLoginData() {
        userDetails = null
        userToken = ""
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


    private fun getUser(): User? {
        val data = preferences.getString(USER, null)
        return data?.let {
            try {
                json.decodeFromString<User>(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun storeUser(user: User?) {
        val jsonData = user?.let { json.encodeToString(it) }
        preferences.edit { putString(USER, jsonData) }
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
        private var _uniqueID: String? = null
    }
}
