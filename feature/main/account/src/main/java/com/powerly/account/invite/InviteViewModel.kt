package com.powerly.account.invite

import androidx.lifecycle.ViewModel
import com.powerly.core.network.DeviceHelper
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class InviteViewModel (
    private val deviceHelper: DeviceHelper
) : ViewModel() {
    val appLink: String get() = deviceHelper.appLink
}