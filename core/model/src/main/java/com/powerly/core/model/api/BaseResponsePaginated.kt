package com.powerly.core.model.api

import com.powerly.core.model.util.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base class for deserializing paginated response for api v3
 */
@Serializable
 class BaseResponsePaginated<T> {
    @SerialName("success")
    private val success: Int? = null

    @SerialName("message")
    private val message: String? = null

    @SerialName(value = "data")
    val data: List<T>? = null

    @SerialName(value = "meta")
    val meta: PaginationMeta? = null

    fun getMessage(): Message = Message(
        value = message.orEmpty(),
        type = if (isSuccess) Message.SUCCESS else Message.ERROR
    )

    val isSuccess: Boolean get() = success == 1
    val hasData: Boolean get() = isSuccess && data != null

}

@Serializable
data class PaginationMeta(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("last_page") val lastPage: Int,
    @SerialName("per_page") val pageSize: Int,
    @SerialName("total") val total: Int,
)

data class ValidationErrorResponse(
    val success: Int,
    val message: String,
    val errors: Map<String, List<String>>?
) {
    val firstError: String get() = errors?.values?.toList()?.getOrNull(0)?.getOrNull(0).orEmpty()
}