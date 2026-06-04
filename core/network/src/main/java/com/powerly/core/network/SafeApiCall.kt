package com.powerly.core.network

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException

/** Wraps a Ktor call and maps its payload + HTTP errors into [ApiStatus]. */
suspend inline fun <T> safeApiCall(
    crossinline block: suspend () -> ApiResponse<T?>,
): ApiStatus<T> = try {
    val response = block()
    if (response.hasData) ApiStatus.Success(response.getData(), response.getMessage())
    else ApiStatus.Error(response.getMessage())
} /*catch (e: CancellationException) {
    throw e
}*/ catch (e: ResponseException) {
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
} /*catch (e: CancellationException) {
    throw e
}*/ catch (e: ResponseException) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
} catch (e: Exception) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
}

/**
 * Like [safeApiAction] but takes a raw [HttpResponse] — for endpoints with no payload
 * whose success is determined by HTTP status alone (e.g. DELETE, logout).
 */
suspend inline fun safeApiActionRaw(
    crossinline block: suspend () -> HttpResponse,
): ApiStatus<Boolean> = try {
    val response = block()
    if (response.isSuccessful) ApiStatus.Success(true)
    else ApiStatus.Error(response.asErrorMessage())
} catch (e: ResponseException) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
} catch (e: Exception) {
    e.printStackTrace()
    ApiStatus.Error(e.asErrorMessage())
}
