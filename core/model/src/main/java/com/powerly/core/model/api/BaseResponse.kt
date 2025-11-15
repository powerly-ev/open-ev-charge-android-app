package com.powerly.core.model.api

import com.powerly.core.model.util.Message
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
open class ApiResponse<T>(
    @JsonNames("data", "results")  private val data: T? = null,
    private val success: Int = 0,
    @JsonNames("message", "msg") private val message: String? = null
) {
    val hasData: Boolean get() = this.data != null
    val isSuccess get() = this.success == 1
    fun getData() = this.data!!
    fun getMessage(code: Int? = null): Message = Message(
        msg = message.orEmpty(),
        type = if (isSuccess) Message.SUCCESS else Message.ERROR,
        code = code
    )
}

