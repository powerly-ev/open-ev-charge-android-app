package com.powerly.core.domain.model.powerly

import java.text.SimpleDateFormat
import java.util.Locale


data class Review(
    val id: Int,
    val user: Reviewer,
    val orderId: Int,
    val content: String?,
    val rating: Double,
    val createdAt: String,
    val updatedAt: String = ""
) {
    fun createdAt(): String = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(createdAt) ?: createdAt
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        createdAt
    }
}

data class Reviewer(
    val id: Int,
    val firstName: String,
    val lastName: String
) {
    val userName: String get() = "$firstName $lastName"
    val photoAlt: String
        get() = if (userName.isNotBlank()) userName[0].uppercase()
        else "P"
}
