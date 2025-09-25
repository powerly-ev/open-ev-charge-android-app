package com.powerly.main.di

import com.powerly.account.di.AccountModule
import com.powerly.home.di.HomeModule
import com.powerly.orders.di.OrdersModule
import com.powerly.scan.di.ScanModule
import org.koin.ksp.generated.module

object MainModules {
    val homeModule = HomeModule().module
    val accountModule = AccountModule().module
    val scanModule = ScanModule().module
    val ordersModule = OrdersModule().module
}