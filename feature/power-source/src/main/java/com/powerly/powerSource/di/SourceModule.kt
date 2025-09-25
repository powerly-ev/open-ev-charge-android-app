package com.powerly.powerSource.di

import com.powerly.charge.di.ChargeModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.powerly.powerSource")
class PowerSourceModule


object PsModules {
    val psModule = PowerSourceModule().module
    val chargeModule = ChargeModule().module
}