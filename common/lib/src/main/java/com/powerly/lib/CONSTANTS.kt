package com.powerly.lib

import android.content.Intent

object CONSTANTS {
    const val NOTIFICATION_TYPE = "notification_type"
    const val POWER_SOURCE_ID = "cp_id"
}

object MainScreen {
    private const val MAIN_DESTINATION = "main_destination"
    fun Intent.getMainDestination(): Route {
        if (this.hasExtra(MAIN_DESTINATION)) {
            val extra = this.getStringExtra(MAIN_DESTINATION)
            this.removeExtra(MAIN_DESTINATION)
            return when (extra) {
                Destination.HOME.name -> AppRoutes.Navigation
                Destination.WELCOME.name -> AppRoutes.User
                else -> AppRoutes.Splash
            }
        } else return AppRoutes.Splash
    }

    fun Intent.setMainScreenHome() {
        this.putExtra(MAIN_DESTINATION, Destination.HOME.name)
    }

    fun Intent.setMainScreenWelcome() {
        this.putExtra(MAIN_DESTINATION, Destination.WELCOME.name)
    }

    private enum class Destination {
        HOME,
        WELCOME
    }
}

object MyPackages {
    const val MAIN = "com.powerly.MainActivity"
}