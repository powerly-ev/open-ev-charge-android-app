package com.powerly.core.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.powerly.core.model.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Manages the storage and retrieval of user and application data.
 * This class handles interactions with DataStore for preferences and a local database,
 * providing a centralized point of access for session data, user details, and app settings.
 *
 * @property context Application context for accessing system resources.
 * @property tokenEncryption Service for encrypting and decrypting sensitive data like user tokens.
 * @property userDataStore DataStore instance for storing user-specific preferences.
 * @property appDataStore DataStore instance for storing application-wide preferences.
 * @property localDataSource Source for local database operations (e.g., caching countries).
 * @property ioDispatcher Coroutine dispatcher for performing I/O operations off the main thread.
 */
class StorageManager(
    private val context: Context,
    private val tokenEncryption: TokenEncryption,
    private val userDataStore: DataStore<Preferences>,
    private val appDataStore: DataStore<Preferences>,
    private val localDataSource: LocalDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * A [Json] instance configured to be lenient with unknown keys and to encode default values.
     * Used for serializing and deserializing objects (like [User]) to/from JSON strings.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Keys for user-specific preferences stored in [userDataStore]. */
    private object UserKeys {
        val USER = stringPreferencesKey("user_details")
        val USER_TOKEN = stringPreferencesKey("user_token_encrypted") // ← Encrypted token
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val CURRENCY_STATIC = booleanPreferencesKey("static_currency")
        val REGISTER_NOTIFICATION = stringPreferencesKey("notification")
        val ON_BOARDING = booleanPreferencesKey("on_boarding")
    }

    /** Keys for application-wide preferences stored in [appDataStore]. */
    private object AppKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val UNIQUE_ID = stringPreferencesKey("unique_id")
    }

    /**
     * In-memory cache for the decrypted user access token to avoid repeated decryption.
     * This property is private and should be accessed via [getUserToken].
     */
    private var _userToken: String? = null

    /**
     * A [Flow] that emits the currently stored [User] object whenever it changes.
     * The user data is deserialized from a JSON string in DataStore.
     * It emits `null` if the user is not stored or if deserialization fails.
     * The flow is configured to emit only distinct values.
     */
    val userFlow: Flow<User?> = userDataStore.data
        .map { prefs ->
            prefs[UserKeys.USER]?.let { jsonStr ->
                try {
                    json.decodeFromString<User>(jsonStr)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        .distinctUntilChanged()

    /**
     * Retrieves the stored [User] object once.
     * This is a suspending function that reads directly from DataStore.
     * @return The deserialized [User] object, or `null` if not found or on error.
     */
    suspend fun getUser(): User? = withContext(ioDispatcher) {
        userDataStore.data.first()[UserKeys.USER]?.let { jsonStr ->
            try {
                json.decodeFromString<User>(jsonStr)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Saves a [User] object to DataStore by serializing it to a JSON string.
     * If the provided user is `null`, it removes the user entry from DataStore.
     *
     * @param user The [User] object to save, or `null` to clear the existing user data.
     */
    suspend fun saveUser(user: User?) = withContext(ioDispatcher) {
        userDataStore.edit { prefs ->
            if (user != null) {
                try {
                    val jsonStr = json.encodeToString(user)
                    prefs[UserKeys.USER] = jsonStr
                } catch (e: Exception) {
                    e.printStackTrace()
                    prefs.remove(UserKeys.USER)
                }
            } else {
                prefs.remove(UserKeys.USER)
            }
        }
    }

    /**
     * Retrieves the Firebase Cloud Messaging (FCM) registration token from DataStore.
     * @return The FCM token as a [String], or an empty string if not found.
     */
    suspend fun getMessagingToken(): String = withContext(ioDispatcher) {
        userDataStore.data.first()[UserKeys.FCM_TOKEN].orEmpty()
    }

    /**
     * Saves the Firebase Cloud Messaging (FCM) registration token to DataStore.
     * @param token The FCM token to save.
     */
    suspend fun setMessagingToken(token: String) = withContext(ioDispatcher) {
        userDataStore.edit { it[UserKeys.FCM_TOKEN] = token }
    }


    /**
     * Retrieves the decrypted user access token.
     * It first checks the in-memory cache ([_userToken]). If the cache is empty,
     * it reads the encrypted token from DataStore, decrypts it, caches it, and then returns it.
     *
     * @return The decrypted user token as a [String], or an empty string if not found.
     */
    suspend fun getUserToken(): String = withContext(ioDispatcher) {
        if (_userToken == null) {
            val encryptedToken = userDataStore.data.first()[UserKeys.USER_TOKEN]
            _userToken = encryptedToken?.let { tokenEncryption.decrypt(it) }
        }
        _userToken.orEmpty()
    }

    /**
     * Encrypts and saves the user access token to DataStore.
     * Also updates the in-memory cache ([_userToken]).
     * If an empty token is provided, the token entry is removed from DataStore.
     *
     * @param token The raw user access token to save.
     */
    suspend fun setUserToken(token: String) = withContext(ioDispatcher) {
        _userToken = token
        userDataStore.edit { prefs ->
            if (token.isEmpty()) {
                prefs.remove(UserKeys.USER_TOKEN)
            } else {
                try {
                    val encrypted = tokenEncryption.encrypt(token)
                    prefs[UserKeys.USER_TOKEN] = encrypted
                } catch (e: Exception) {
                    e.printStackTrace()
                    prefs.remove(UserKeys.USER_TOKEN)
                }
            }
        }
    }


    /**
     * A quick, non-suspending check to see if a user token is present in the in-memory cache.
     * Useful for synchronous checks after the token has likely been loaded.
     * For a more reliable check, use [isLoggedIn()].
     */
    val isLoggedIn: Boolean get() = _userToken?.isNotEmpty() == true

    /**
     * A reliable, non-suspending check to determine if the user is logged in.
     * It does this by checking if the user token is not empty.
     */
    suspend fun isLoggedIn() = getUser() != null && getUserToken().isNotEmpty()

    /**
     * A [Flow] that emits `true` if the user is logged in (i.e., has a token), and `false` otherwise.
     * This flow is triggered whenever the user token changes in DataStore.
     */
    val loggedInFlow: Flow<Boolean> = userDataStore.data
        .map { it[UserKeys.USER_TOKEN].orEmpty().isNotEmpty() }
        .distinctUntilChanged()


    /**
     * Retrieves the user's preferred currency.
     * It first tries to get the currency from the logged-in user's profile.
     * If that fails, it falls back to the currency setting stored directly in DataStore.
     *
     * @return The currency code as a [String], or an empty string if not found.
     */
    suspend fun getCurrency(): String = withContext(ioDispatcher) {
        getUser()?.currency.orEmpty()
    }

    /**
     * A [Flow] that emits the user's currency.
     */
    val currencyFlow: Flow<String> = flow { emit(getCurrency()) }

    /**
     * Retrieves the application's currently selected language.
     * Falls back to the device's default locale if no language is set.
     * @return The language code (e.g., "en").
     */
    suspend fun getCurrentLanguage(): String = withContext(ioDispatcher) {
        appDataStore.data.first()[AppKeys.LANGUAGE] ?: getDefaultAppLocale(context)
    }

    /**
     * Sets the application's current language in DataStore.
     * @param language The language code to save (e.g., "en").
     */
    suspend fun setCurrentLanguage(language: String) = withContext(ioDispatcher) {
        appDataStore.edit { it[AppKeys.LANGUAGE] = language }
    }

    /**
     * A [Flow] that emits the current application language whenever it changes.
     * Falls back to the default locale.
     */
    val languageFlow: Flow<String> = appDataStore.data
        .map { it[AppKeys.LANGUAGE] ?: getDefaultAppLocale(context) }
        .distinctUntilChanged()

    /**
     * Retrieves a unique identifier for the app installation.
     * If no ID exists, it generates a new UUID, saves it, and then returns it.
     * This ID persists across app launches.
     *
     * @return A unique [String] identifier.
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getUniqueId(): String = withContext(ioDispatcher) {
        var uniqueId = appDataStore.data.first()[AppKeys.UNIQUE_ID]
        if (uniqueId == null) {
            uniqueId = Uuid.random().toString()
            appDataStore.edit { it[AppKeys.UNIQUE_ID] = uniqueId }
        }
        uniqueId
    }


    /** Retrieves the logged-in user's country ID once. */
    suspend fun getCountryId(): Int? = withContext(ioDispatcher) {
        getUser()?.countryId
    }

    suspend fun shouldShowRegisterNotification(): Boolean = withContext(ioDispatcher) {
        !isLoggedIn() && (userDataStore.data.first()[UserKeys.REGISTER_NOTIFICATION]
            ?: "0") == "0"
    }

    suspend fun setShowRegisterNotification(show: Boolean) = withContext(ioDispatcher) {
        userDataStore.edit { it[UserKeys.REGISTER_NOTIFICATION] = if (show) "0" else "1" }
    }

    /**
     * Checks if the on-boarding screens should be displayed.
     * Defaults to `true` if the preference is not set.
     * @return `true` if on-boarding should be shown, `false` otherwise.
     */
    suspend fun shouldShowOnBoarding(): Boolean = withContext(ioDispatcher) {
        userDataStore.data.first()[UserKeys.ON_BOARDING] ?: true
    }

    /**
     * Sets the preference for whether to show the on-boarding screens.
     * @param show Set to `false` to indicate that on-boarding has been completed.
     */
    suspend fun setShowOnBoarding(show: Boolean) = withContext(ioDispatcher) {
        userDataStore.edit { it[UserKeys.ON_BOARDING] = show }
    }

    /**
     * Checks if the user's currency is "pinned" or static.
     * Defaults to `false` if the preference is not set.
     * @return `true` if the currency is pinned, `false` otherwise.
     */
    suspend fun isPinUserCurrency(): Boolean = withContext(ioDispatcher) {
        userDataStore.data.first()[UserKeys.CURRENCY_STATIC] ?: false
    }

    /**
     * Sets the preference for pinning the user's currency.
     * @param pinned `true` to pin the currency, `false` to unpin it.
     */
    suspend fun setPinUserCurrency(pinned: Boolean) = withContext(ioDispatcher) {
        userDataStore.edit { it[UserKeys.CURRENCY_STATIC] = pinned }
    }

    /**
     * A convenience method to handle all necessary operations after a successful login.
     * It saves the user token, the user profile, and the user's currency.
     *
     * @param user The [User] object received upon login, which must contain an access token.
     */
    suspend fun saveLogin(user: User) = withContext(ioDispatcher) {
        val token = user.accessToken ?: return@withContext
        setUserToken(token)
        saveUser(user)
    }

    /**
     * Clears all login-related data from the application.
     * This includes clearing the in-memory token, removing the user from DataStore,
     * clearing all user preferences, and clearing any cached country data from the local database.
     * This is effectively a "log out" operation.
     */
    suspend fun clearLoginData() = withContext(ioDispatcher) {
        setUserToken("")
        saveUser(null)
        userDataStore.edit { it.clear() }
        localDataSource.clearCountry()
    }

    /**
     * Clears cached country data from the local database.
     */
    suspend fun clearCountry() = withContext(ioDispatcher) {
        localDataSource.clearCountry()
    }
}
