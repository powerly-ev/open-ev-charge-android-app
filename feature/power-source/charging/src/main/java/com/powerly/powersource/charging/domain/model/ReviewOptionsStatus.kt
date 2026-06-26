package com.powerly.powersource.charging.domain.model

import com.powerly.core.domain.model.Message

sealed class ReviewOptionsStatus {
    data class Error(val msg: Message) : ReviewOptionsStatus()
    data class Success(val reasons: Map<String, List<String>>) : ReviewOptionsStatus()
    data object Loading : ReviewOptionsStatus()
}
