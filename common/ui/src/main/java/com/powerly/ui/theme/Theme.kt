package com.powerly.ui.theme

import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import java.util.Locale


@Composable
fun AppTheme(
    language: String = "en",
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ConfigureSystemBars(darkTheme)
    val isArabic = language == "ar"

    val colorScheme = lightColorScheme(
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
    )

    val typography = myTypography(isArabic)
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    } else {
        val context = LocalContext.current
        val activity = LocalActivity.current as? ComponentActivity
        val locale = remember(language) { language.toLocale() }
        val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
        val density = Density(LocalDensity.current.density, fontScale = 1f)

        DisposableEffect(language) {
            Locale.setDefault(locale)
            activity?.let { act ->
                Configuration(act.resources.configuration).apply {
                    setLocale(locale)
                    setLayoutDirection(locale)
                    fontScale = 1f
                }.let { config ->
                    @Suppress("DEPRECATION")
                    act.resources.updateConfiguration(config, act.resources.displayMetrics)
                }
            }

            onDispose { }
        }

        if (activity != null) {
            val localizedContext = remember(locale) {
                val config = Configuration(activity.resources.configuration).apply {
                    setLocale(locale)
                    setLayoutDirection(locale)
                    fontScale = 1f
                }
                Locale.setDefault(locale)
                context.createConfigurationContext(config)
            }
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides activity,
                LocalMainActivity provides activity,
                LocalDensity provides density,
                LocalLayoutDirection provides layoutDirection
            ) {
                MaterialTheme(
                    colorScheme = colorScheme,
                    typography = typography,
                    content = content
                )
            }
        } else {
            CompositionLocalProvider(
                LocalDensity provides density,
                LocalLayoutDirection provides layoutDirection
            ) {
                MaterialTheme(
                    colorScheme = colorScheme,
                    typography = typography,
                    content = content
                )
            }
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