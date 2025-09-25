package com.powerly

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.powerly.account.di.AccountModule
import com.powerly.core.data.di.DataModule
import com.powerly.core.database.di.DatabaseModule
import com.powerly.core.network.di.NetworkModule
import com.powerly.lib.di.LibModule
import com.powerly.main.di.MainModules
import com.powerly.orders.di.OrdersModule
import com.powerly.payment.di.PaymentModule
import com.powerly.powerSource.di.PsModules
import com.powerly.splash.di.SplashModule
import com.powerly.user.di.UserModule
import com.powerly.vehicles.di.VehiclesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@App)
            modules(
                LibModule().module,
                DatabaseModule().module,
                NetworkModule().module,
                DataModule().module,
                PaymentModule().module,
                OrdersModule().module,
                AccountModule().module,
                MainModules.homeModule,
                MainModules.ordersModule,
                MainModules.accountModule,
                MainModules.scanModule,
                VehiclesModule().module,
                SplashModule().module,
                UserModule().module,
                VehiclesModule().module,
                PsModules.psModule,
                PsModules.chargeModule,
                defaultModule,
            )

        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}


