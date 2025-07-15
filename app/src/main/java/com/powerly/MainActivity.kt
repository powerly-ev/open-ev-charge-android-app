package com.powerly

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import com.powerly.lib.MainScreen.getMainDestination
import com.powerly.lib.managers.LocaleManager
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initUiState()
        setContent {
            AppTheme {
                RootGraph(
                    startDestination = intent.getMainDestination(),
                    modifier = Modifier.systemBarsPadding(),
                )
            }
        }
        //force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        viewModel.initPaymentManager(this)
    }


    override fun attachBaseContext(base: Context) {
        // you can't access dagger injected dependency before onCreate
        val lang = StorageManager.instance.currentLanguage
        super.attachBaseContext(LocaleManager.instance.setLocale(base, lang))
    }
}


