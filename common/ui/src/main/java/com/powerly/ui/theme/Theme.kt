package com.powerly.ui.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.powerly.ui.extensions.isArabic


private val appColorScheme = lightColorScheme(
    primary = ColorGuide1.primary,
    onPrimary = ColorGuide1.onPrimary,
    secondary = ColorGuide1.secondary,
    onSecondary = ColorGuide1.onSecondary,
    tertiary = ColorGuide1.tertiary,
    onTertiary = ColorGuide1.onTertiary,
    background = ColorGuide1.background,
    onBackground = ColorGuide1.onBackground,
    surface = ColorGuide1.surface,
    onSurface = ColorGuide1.onSurface,
    onError = ColorGuide1.secondary,
    error = ColorGuide1.errorColor
)

@Suppress("DEPRECATION")
@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    whiteSystemBars: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val window = LocalActivity.current?.window
    if (view.isInEditMode.not() && window != null) {
        SideEffect {
            val systemBarsColor = if (whiteSystemBars) Color.White.toArgb()
            else MyColors.background.toArgb()

            window.statusBarColor = systemBarsColor
            window.navigationBarColor = systemBarsColor

            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = darkTheme.not()
            windowInsetsController.isAppearanceLightNavigationBars = darkTheme.not()
        }
    }

    MaterialTheme(
        colorScheme = appColorScheme,
        typography = myTypography(isArabic()),
        content = content
    )
}


