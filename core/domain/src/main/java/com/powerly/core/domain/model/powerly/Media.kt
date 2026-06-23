package com.powerly.core.domain.model.powerly

data class Media(
    val id: Int = 0,
    val title: String = "",
    val url: String = "",
    val type: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
