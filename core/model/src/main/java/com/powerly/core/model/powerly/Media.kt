package com.powerly.core.model.powerly

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName

class MediaResponse : BaseResponse<List<Media>?>()

data class Media(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
)