package com.powerly.core.network

import android.util.Log
import com.powerly.core.database.StorageManager
import com.powerly.core.model.api.FlexibleBooleanSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.annotation.Single
import java.util.concurrent.TimeUnit


@Single
class KtorClient(
    private val deviceHelper: DeviceHelper,
    private val storageManager: StorageManager
) {

    val client: HttpClient

    init {
        Log.v(TAG, "init..")
        client = HttpClient(OkHttp) {
            expectSuccess = true

            // ✅ Timeout setup
            engine {
                config {
                    connectTimeout(60, TimeUnit.SECONDS)
                    readTimeout(60, TimeUnit.SECONDS)
                }
            }

            // ✅ JSON serialization setup
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        coerceInputValues = true
                        prettyPrint = true
                        encodeDefaults = false
                        serializersModule = SerializersModule {
                            //contextual(Int::class, FlexibleIntSerializer)
                            //contextual(Double::class, FlexibleDoubleSerializer)
                            contextual(Boolean::class, FlexibleBooleanSerializer)
                        }
                    }
                )
            }

            // ✅ Logging (similar to HttpLoggingInterceptor)
            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("Ktor", message)
                    }
                }
            }

            // ✅ Add default headers for every request
            install(DefaultRequest) {
                url(deviceHelper.apiBaseUrl)
                header("API-KEY", deviceHelper.apiKey)
                header("App-Type", deviceHelper.appType.toString())
                header("App-Version", deviceHelper.appVersion)
                header("Device-Type", deviceHelper.deviceType.toString())
                header("Device-Model", deviceHelper.deviceModel())
                header("Device-Version", deviceHelper.deviceVersion)
                contentType(ContentType.Application.Json)
            }
        }

        client.sendPipeline.intercept(HttpSendPipeline.State) {
            val userToken = storageManager.userToken
            if (userToken.isNotEmpty()) {
                context.headers.append("Authorization", "Bearer $userToken")
            }
            val defaultLocale = storageManager.currentLanguage
            val apiSupportedLocales = listOf("en", "ar", "es", "fr")
            val acceptLanguage = if (apiSupportedLocales.contains(defaultLocale)) defaultLocale
            else "en"
            context.headers.append("Accept-Language", acceptLanguage)
        }
    }

    companion object {
        private const val TAG = "KtorClient"
    }
}