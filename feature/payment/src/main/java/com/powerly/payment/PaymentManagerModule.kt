package com.powerly.payment

import android.content.Context
import com.powerly.core.network.DeviceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityRetainedComponent::class)
object PaymentManagerModule {
    @Provides
    fun providePaymentManager(
        @ApplicationContext context: Context,
        deviceHelper: DeviceHelper
    ): PaymentManager {
        return PaymentManager(
            publishableKey = deviceHelper.publishableKey,
            context = context
        )
    }
}