package com.powerly.core.network

import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.Locale

class DeviceHelper(private val context: Context) {
    companion object {
        private const val TAG = "DeviceHelper"
    }

    suspend fun isOnline(): Boolean = withContext(Dispatchers.IO) {
        if (isDemo) true
        else try {
            val timeoutMs = 5000
            val sock = Socket()
            val scSocketAddress: SocketAddress = InetSocketAddress("8.8.8.8", 53)
            withContext(Dispatchers.IO) {
                sock.connect(scSocketAddress, timeoutMs)
                sock.close()
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    val appVersion: String = BuildConfig.APP_VERSION
    val deviceModel: String get() = deviceModel()
    val deviceType: Int = 1
    val buildType: String get() = BuildConfig.BUILD_TYPE
    val apiKey = BuildConfig.API_API_KEY
    val apiBaseUrl = BuildConfig.API_BASE_URL
    val privacyPolicyUrl = BuildConfig.PRIVACY_POLICY_URL
    val termsAndConditionsUrl = BuildConfig.TERMS_AND_CONDITIONS_URL
    val supportNumber = BuildConfig.SUPPORT_NUMBER
    val appType = BuildConfig.APP_TYPE
    val deviceVersion: String = Build.VERSION.RELEASE

    val isGoogle: Boolean get() = true
    val isArabic: Boolean get() = Locale.getDefault().language.contains("ar")
    val isTest: Boolean get() = BuildConfig.isTest ?: false
    val isDemo: Boolean get() = BuildConfig.FLAVOR.contains("demo")
    val isDebug: Boolean get() = BuildConfig.DEBUG ?: false
    val googlePlacesApiKey: String get() = BuildConfig.PLACES_API_KEY
    val publishableKey: String get() = BuildConfig.STRIP_PUBLISHABLE_KEY
    val supportMap: Boolean get() = googlePlacesApiKey.isNotBlank()
    val appLink get() = "https://play.google.com/store/apps/details?id=${context?.packageName}"


    fun deviceModel(): String {
        val manufacturer = Build.MANUFACTURER
        val buildModel = Build.MODEL
        return if (buildModel.startsWith(manufacturer, ignoreCase = true)) capitalize(buildModel)
        else capitalize(manufacturer) + " " + buildModel
    }

    private fun capitalize(s: String?): String {
        if (s.isNullOrEmpty()) return ""
        val first = s[0]
        return if (Character.isUpperCase(first)) s
        else first.uppercaseChar().toString() + s.substring(1)
    }
}