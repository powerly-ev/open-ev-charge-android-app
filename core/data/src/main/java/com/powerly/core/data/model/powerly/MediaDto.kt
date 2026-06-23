package com.powerly.core.data.model.powerly

import com.powerly.core.domain.model.powerly.Media
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

fun MediaDto.toDomain(): Media = Media(
    id = id,
    title = title,
    url = url,
    type = type,
    createdAt = createdAt,
    updatedAt = updatedAt
)
