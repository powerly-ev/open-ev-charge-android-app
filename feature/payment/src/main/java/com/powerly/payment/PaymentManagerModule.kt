package com.powerly.payment

import android.content.Context
import com.powerly.core.network.DeviceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentManagerModule {
    @Singleton
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