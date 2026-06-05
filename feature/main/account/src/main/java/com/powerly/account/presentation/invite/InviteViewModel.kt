package com.powerly.account.presentation.invite

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.model.AppInfo
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class InviteViewModel(
    private val appInfo: AppInfo
) : ViewModel() {
    val appLink: String get() = appInfo.appLink
}
