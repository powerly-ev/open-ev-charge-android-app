package com.powerly.core.analyticsImpl

import android.content.Context
import com.powerly.core.analytics.EventsManager
import com.powerly.core.analytics.UserIdentifier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val isDebug: Boolean,
    private val identifier: UserIdentifier?
) : EventsManager {
    override fun log(event: String, key: String?, value: String?) {
    }

    override fun updatePushToken(token: String) {
    }

}
