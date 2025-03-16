package com.powerly.core.network

import com.powerly.core.model.api.ValidationErrorResponse
import com.powerly.core.model.util.Message
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response


val Exception.asErrorMessage: Message get() = Message(this.message.orEmpty(), Message.ERROR)
val Throwable.asErrorMessage: Message get() = Message(this.message.orEmpty(), Message.ERROR)

val HttpException.asErrorMessage: Message
    get() {
        var msg = this.message.orEmpty()
        this.response()?.errorBody()?.let { errorBody ->
            try {
                val jObjError = JSONObject(errorBody.string())
                if (jObjError.has("message")) msg = jObjError.get("message").toString()
                else if (jObjError.has("msg")) msg = jObjError.get("msg").toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return Message(msg, Message.ERROR, code = code())
    }

val Response<*>.asErrorMessage: Message
    get() {
        val msg = try {
            val jObjError = JSONObject(this.errorBody()!!.string())
            if (jObjError.has("message")) jObjError.get("message").toString()
            else if (jObjError.has("msg")) jObjError.get("msg").toString()
            else ""
        } catch (e: Exception) {
            e.printStackTrace()
            e.message.orEmpty()
        }
        return Message(msg, Message.ERROR, code = this.code())
    }

val Headers.needToRefreshToken: Boolean get() = (this["Token-Refresh-Required"] ?: "0") == "1"

val HttpException.asValidationErrorMessage: Message
    get() {
        val msg: String = try {
            // extract error message body as json like that
            //{"success":0,"message":"...","errors":{"last_name":["..."]}}
            val errorBody = JSONObject(response()?.errorBody()!!.string()).toString()
            // deserialize json
            val errorResponse = Gson().fromJson(
                errorBody,
                ValidationErrorResponse::class.java
            )
            // extract first validation error message if exists
            if (errorResponse.errors.isNullOrEmpty()) errorResponse.message
            else errorResponse.firstError
        } catch (e: Exception) {
            e.printStackTrace()
            this.message.orEmpty()
        }
        return Message(msg, Message.ERROR, code = code())
    }
