package com.powerly.core.model.powerly

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale


@Serializable
data class Review(
    @SerialName("id") val id: Int,
    @SerialName("user") val user: Reviewer,
    @SerialName("order_id") val orderId: Int,
    @SerialName("content") val content: String?,
    @SerialName("rating") val rating: Double,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String = ""
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

@Serializable
data class Reviewer(
    @SerialName("id") val id: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
) {
    val userName: String get() = "$firstName $lastName"
    val photoAlt: String
        get() = if (userName.isNotBlank()) userName[0].uppercase()
        else "P"
}

@Serializable
data class ReviewBody(
    @SerialName("rating") val rating: Double,
    @SerialName("content") val msg: String,
)

