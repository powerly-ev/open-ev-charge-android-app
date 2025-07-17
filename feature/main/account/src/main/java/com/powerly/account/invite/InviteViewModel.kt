package com.powerly.account.invite

import androidx.lifecycle.ViewModel
import com.powerly.core.network.DeviceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val deviceHelper: DeviceHelper
) : ViewModel() {
    val appLink: String get() = deviceHelper.appLink
}