package com.powerly.splash

sealed interface SplashAction {
    data object OpenHomeScreen : SplashAction
    data object OpenWelcomeScreen : SplashAction
    data object UpdateApp : SplashAction
    data object Maintenance : SplashAction
    data object NoInternet : SplashAction
    data class TryAgain(val message: String) : SplashAction
}