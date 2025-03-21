package com.powerly.map.home

import com.powerly.core.model.powerly.PowerSource
import com.powerly.map.home.slider.SliderAction
import com.powerly.ui.dialogs.signIn.SignInOptions

internal sealed class HomeEvents {
    data class SliderClick(val action: SliderAction) : HomeEvents()
    data class OpenPowerSource(val source: PowerSource) : HomeEvents()
    data class OpenMap(val source: PowerSource? = null) : HomeEvents()
    data object RequestLocation : HomeEvents()
    data object OnLogin : HomeEvents()
    data object OnSupport : HomeEvents()
    data object OnBalance : HomeEvents()
}

sealed class NavigationEvents {
    data class Map(val source: PowerSource?) : NavigationEvents()
    data class SourceDetails(val source: PowerSource) : NavigationEvents()
    data class Login(val options: SignInOptions) : NavigationEvents()
    data object Balance : NavigationEvents()
    data object Support : NavigationEvents()
}

