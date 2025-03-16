package com.powerly.core.model.util

data class Message(
    val msg: String = "",
    val type: Int = SUCCESS,
    var code: Int? = null
) {
    companion object {
        const val ERROR = 0
        const val SUCCESS = 1
    }

    val isError: Boolean get() = type == ERROR
}

val String.asErrorMessage get() = Message(this, Message.ERROR)