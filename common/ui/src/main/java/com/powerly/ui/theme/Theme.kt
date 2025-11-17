package com.powerly.ui.theme

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.powerly.resources.R
import com.powerly.ui.extensions.isArabic
import java.util.Locale


@Composable
fun AppTheme(
    language: String = "en",
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ConfigureSystemBars(darkTheme)

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    if (isPreview) {
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
    } else {
        LaunchedEffect(language) {
            Log.v("AppTheme", "language = $language")
        }
        val locale = language.toLocale()
        val localizedContext = remember(locale) {
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            config.fontScale = 1f
            Locale.setDefault(locale)
            context.createConfigurationContext(config)
        }

        val activity = LocalActivity.current as ComponentActivity
        val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
        val density = Density(density = LocalDensity.current.density, fontScale = 1f)
        CompositionLocalProvider(
            LocalActivityResultRegistryOwner provides activity,
            LocalMainActivity provides activity,
            LocalDensity provides density,
            LocalContext provides localizedContext,
            LocalLayoutDirection provides layoutDirection
        ) {
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
    }
}


@Suppress("DEPRECATION")
@Composable
private fun ConfigureSystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    val window = LocalActivity.current?.window
    if (view.isInEditMode.not() && window != null) {
        SideEffect {
            val systemBarsColor = if (darkTheme) MyColors.background.toArgb()
            else Color.White.toArgb()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = systemBarsColor
                window.navigationBarColor = systemBarsColor
            }
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = darkTheme.not()
            windowInsetsController.isAppearanceLightNavigationBars = darkTheme.not()
        }
    }
}


val LocalMainActivity = staticCompositionLocalOf<ComponentActivity?> { null }

fun String.toLocale(): Locale {
    return if (this.contains("-r")) {
        val (language, region) = this.split("-r")
        Locale.Builder()
            .setLanguage(language)
            .setRegion(region.uppercase())
            .build()
    } else {
        Locale.Builder()
            .setLanguage(this)
            .build()
    }
}