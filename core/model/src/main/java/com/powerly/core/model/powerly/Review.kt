package com.powerly.core.model.powerly

import com.powerly.core.model.api.BaseResponse
import com.powerly.core.model.api.BaseResponsePaginated
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewResponse : BaseResponsePaginated<Review>()

data class Review(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: Reviewer,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("content") val content: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String = ""
) {
    fun createdAt(): String = try {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        // Parse the input date string
        val date = inputFormat.parse(createdAt) ?: createdAt
        // Format the date as per the output format
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        createdAt
    }
}

data class Reviewer(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
) {
    val userName: String get() = "$firstName $lastName"
    val photoAlt: String
        get() = if (userName.isNotBlank()) userName.substring(0, 1).uppercase()
        else "P"
}


class ReviewOptionsResponse : BaseResponse<Map<String, List<String>>?>()

class ReviewsResponse : BaseResponse<List<Session>?>()

class ReviewAddResponse : BaseResponse<Session?>()
data class ReviewBody(
    @SerializedName("rating") val rating: Double,
    @SerializedName("content") val msg: String,
)

