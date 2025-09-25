package com.powerly.payment.di

import android.content.Context
import com.powerly.core.network.DeviceHelper
import com.powerly.payment.PaymentManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.powerly.payment")
class PaymentModule {
    @Single
    fun providePaymentManager(
        applicationContext: Context,
        deviceHelper: DeviceHelper
    ): PaymentManager {
        return PaymentManager(
            publishableKey = deviceHelper.publishableKey,
            context = applicationContext
        )
    }
}