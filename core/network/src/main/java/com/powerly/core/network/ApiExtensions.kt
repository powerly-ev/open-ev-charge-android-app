package com.powerly.core.network

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.util.Message
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.isSuccess


fun Exception.asErrorMessage(): Message = Message(this.message.orEmpty(), Message.ERROR)

val HttpResponse.isSuccessful: Boolean get() = this.status.isSuccess()

suspend fun HttpResponse.asErrorMessage(): Message {
    val response = this.body<ApiResponse<String?>>()
    val code = this.status.value
    return response.getMessage(code)
}

val ResponseException.code get() = this.response.status.value

suspend fun ResponseException.asErrorMessage(): Message {
    return try {
        val response = this.response.body<ApiResponse<String?>>()
        response.getMessage(code)
    } catch (e: Exception) {
        e.printStackTrace()
        e.asErrorMessage()
    }
}

suspend inline fun <reified T> ResponseException.asTypedErrorMessage(): Message {
    return try {
        val response = this.response.body<ApiResponse<T?>>()
        response.getMessage(code)
    } catch (e: Exception) {
        e.asErrorMessage()
    }
}

fun Headers.needToRefreshToken(): Boolean = (this["Token-Refresh-Required"] ?: "0") == "1"
