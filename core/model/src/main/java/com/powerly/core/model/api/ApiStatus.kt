package com.powerly.core.model.api

import com.powerly.core.model.util.Message

sealed class ApiStatus<out T> {
    data class Error(val msg: Message) : ApiStatus<Nothing>()
    data class Success<T>(val data: T, val msg: Message? = null) : ApiStatus<T>()
    data object Loading : ApiStatus<Nothing>()

    val isSuccessful: Boolean get() = this is Success
}

/** Transforms the success payload while preserving [ApiStatus.Error] and [ApiStatus.Loading]. */
inline fun <T, R> ApiStatus<T>.map(transform: (T) -> R): ApiStatus<R> = when (this) {
    is ApiStatus.Success -> ApiStatus.Success(transform(data))
    is ApiStatus.Error -> this
    is ApiStatus.Loading -> this
}
