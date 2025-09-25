package com.powerly.core.network

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

class RetrofitClient(
    private val errorMessage: String,
    private val deviceHelper: DeviceHelper
) {
    private var serviceClient: RemoteDataSource? = null
    private var userToken: String = ""

    fun initClients(userToken: String) {
        Log.i(TAG, "initRetrofitClients")
        this.userToken = userToken
        serviceClient = null
    }

    val client: RemoteDataSource
        get() {
            if (serviceClient == null) {
                val client = okHttpClientDefault()
                val retrofit = getRetrofit(client, deviceHelper.apiBaseUrl)
                serviceClient = retrofit.create(RemoteDataSource::class.java)
            }
            return serviceClient!!
        }


    private fun okHttpClientDefault(attest: Boolean = false): OkHttpClient {
        Log.i(TAG, "okHttpClientDefault")

        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor())
            .addNetworkInterceptor( //show server logs on debugging only..
                HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                )
            )

        return clientBuilder.build()
    }


    private fun interceptor() = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder().apply {
            method(original.method, original.body)
            val defaultLocale = Locale.getDefault().language
            val apiSupportedLocales = listOf("en", "ar", "es", "fr")
            val acceptLanguage = if (apiSupportedLocales.contains(defaultLocale)) defaultLocale
            else "en"
            header("Accept-Language", acceptLanguage)
            header("API-KEY", deviceHelper.apiKey)
            header("App-Type", deviceHelper.appType.toString())
            header("App-Version", deviceHelper.appVersion)
            header("Device-Type", deviceHelper.deviceType.toString())
            header("Device-Model", deviceHelper.deviceModel())
            header("Device-Version", deviceHelper.deviceVersion)
            if (userToken.isNotEmpty()) header("Authorization", "Bearer $userToken")
        }.build()
        try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "interceptor - ${e.message.orEmpty()}")
            e.printStackTrace()
            Response.Builder()
                .request(original)
                .protocol(Protocol.HTTP_1_1)
                .code(504) // Gateway Timeout
                .message(errorMessage)
                .build()
        }
    }


    private fun getRetrofit(
        client: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        val gson = GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        private const val TAG = "RetrofitClient"
    }
}