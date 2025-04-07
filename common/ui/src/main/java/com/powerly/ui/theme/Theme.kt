package com.powerly.ui.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.powerly.ui.extensions.isArabic
import com.powerly.resources.R


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
        colorScheme = lightColorScheme(
            primary = colorResource(R.color.colorPrimary),
            onPrimary = ColorGuide1.onPrimary,
            secondary = colorResource(R.color.colorSecondary),
            onSecondary = ColorGuide1.onSecondary,
            tertiary = colorResource(R.color.colorTertiary),
            onTertiary = ColorGuide1.onTertiary,
            background = ColorGuide1.background,
            onBackground = ColorGuide1.onBackground,
            surface = ColorGuide1.surface,
            onSurface = ColorGuide1.onSurface,
            onError = colorResource(R.color.colorSecondary),
            error = ColorGuide1.errorColor
        ),
        typography = myTypography(isArabic()),
        content = content
    )
}


