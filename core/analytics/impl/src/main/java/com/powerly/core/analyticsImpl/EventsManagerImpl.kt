package com.powerly.core.analyticsImpl

import android.content.Context
import com.powerly.core.analytics.EventsManager
import com.powerly.core.analytics.UserIdentifier

// Constructed via ManagersModule.provideEventsManager (which supplies isDebug); no @Single
// here, otherwise Koin would also auto-register it and try to resolve isDebug: Boolean from
// the graph, which isn't provided.
class EventsManagerImpl(
    private val context: Context,
    private val isDebug: Boolean,
    private val identifier: UserIdentifier?
) : EventsManager {
    override fun log(event: String, key: String?, value: String?) {
    }

    override fun updatePushToken(token: String) {
    }

}
