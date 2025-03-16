package com.powerly.core.model.api

import com.powerly.core.model.util.Message
import com.google.gson.annotations.SerializedName

/**
 * Base class for deserializing paginated response for api v3
 */
open class BaseResponsePaginated<T> {
    @SerializedName("success")
    private val success: Int? = null

    @SerializedName("message")
    private val message: String? = null

    @SerializedName(value = "data")
    val data: List<T>? = null

    @SerializedName(value = "meta")
    val meta: PaginationMeta? = null

    fun getMessage(): Message = Message(
        msg = message.orEmpty(),
        type = if (isSuccess) Message.SUCCESS else Message.ERROR
    )

    val isSuccess: Boolean get() = success == 1
    val hasData: Boolean get() = isSuccess && data != null

}


data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val pageSize: Int,
    @SerializedName("total") val total: Int,
)

data class ValidationErrorResponse(
    val success: Int,
    val message: String,
    val errors: Map<String, List<String>>?
) {
    val firstError: String get() = errors?.values?.toList()?.getOrNull(0)?.getOrNull(0).orEmpty()
}