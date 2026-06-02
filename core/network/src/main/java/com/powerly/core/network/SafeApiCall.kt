package com.powerly.core.network

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException

/** Wraps a Ktor call and maps its payload + HTTP errors into [ApiStatus]. */
suspend inline fun <T> safeApiCall(
    crossinline block: suspend () -> ApiResponse<T?>,
): ApiStatus<T> = try {
    val response = block()
    if (response.hasData) ApiStatus.Success(response.getData()!!)
    else ApiStatus.Error(response.getMessage())
} catch (e: CancellationException) {
    throw e
} catch (e: ResponseException) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
} catch (e: Exception) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
}

/** Like [safeApiCall] but reports success via [ApiResponse.isSuccess] (no payload). */
suspend inline fun safeApiAction(
    crossinline block: suspend () -> ApiResponse<*>,
): ApiStatus<Boolean> = try {
    val response = block()
    if (response.isSuccess) ApiStatus.Success(true)
    else ApiStatus.Error(response.getMessage())
} catch (e: CancellationException) {
    throw e
} catch (e: ResponseException) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
} catch (e: Exception) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
}
