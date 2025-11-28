package com.powerly.ui

import androidx.compose.runtime.mutableStateOf
import com.powerly.core.network.DeviceHelper

class HomeUiState(val deviceHelper: DeviceHelper? = null) {
    val appVersion = String.format(
        "Version : %s %s", deviceHelper?.buildType, deviceHelper?.appVersion
    )
    val hasSupportNumber = deviceHelper?.supportNumber?.isNotBlank() ?: true
    val languageCode = mutableStateOf("en")
    val isLoggedIn = mutableStateOf(false)
    val supportMap = mutableStateOf(deviceHelper?.supportMap ?: false)
    val userName = mutableStateOf("")
    val balance = mutableStateOf("0.00")
    val currency = mutableStateOf("SAR")
}

