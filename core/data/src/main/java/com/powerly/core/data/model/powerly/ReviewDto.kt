package com.powerly.core.data.model.powerly

import com.powerly.core.domain.model.powerly.Review
import com.powerly.core.domain.model.powerly.Reviewer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReviewDto(
    @SerialName("id") val id: Int,
    @SerialName("user") val user: ReviewerDto,
    @SerialName("order_id") val orderId: Int,
    @SerialName("content") val content: String?,
    @SerialName("rating") val rating: Double,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String = ""
)

@Serializable
internal data class ReviewerDto(
    @SerialName("id") val id: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)

internal fun ReviewDto.toDomain(): Review = Review(
    id = id,
    user = user.toDomain(),
    orderId = orderId,
    content = content,
    rating = rating,
    createdAt = createdAt,
    updatedAt = updatedAt
)

internal fun ReviewerDto.toDomain(): Reviewer = Reviewer(
    id = id,
    firstName = firstName,
    lastName = lastName
)
