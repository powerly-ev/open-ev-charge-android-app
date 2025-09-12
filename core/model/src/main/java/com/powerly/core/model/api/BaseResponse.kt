package com.powerly.core.model.api

import com.powerly.core.model.util.Message
import com.google.gson.annotations.SerializedName

/**
 * Base class for deserializing json response for api v1
 */
open class BaseResponse<T> {
    @SerializedName("success")
    private val success: Int? = null

    @SerializedName("message", alternate = ["msg"])
    private val message: String? = null

    @SerializedName(value = "data", alternate = ["results"])
    private val data: T? = null

    fun getMessage(): Message = Message(
        msg = message.orEmpty(),
        type = if (isSuccess) Message.SUCCESS else Message.ERROR
    )

    val isSuccess: Boolean get() = success == 1
    val hasData: Boolean get() = isSuccess && data != null
    val hasOnlyData: Boolean get() = data != null && (success == null || success == 1)

    val getData: T get() = data!!
}

