package com.powerly.core.domain.model

import com.powerly.core.model.powerly.Media
import com.powerly.core.domain.model.Message

sealed class MediaStatus {
    data class Error(val msg: Message) : MediaStatus()
    data class Success(
        val media: List<Media>,
        val upload: Boolean = false,
        val remove: Boolean = false
    ) : MediaStatus()

    data object Loading : MediaStatus()
}
