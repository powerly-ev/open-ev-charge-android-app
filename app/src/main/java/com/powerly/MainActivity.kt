package com.powerly

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import com.powerly.lib.MainScreen.getMainDestination
import com.powerly.lib.managers.LocaleManager
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.theme.AppTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val storageManager: StorageManager by inject()
    private val localeManager: LocaleManager by inject()

    private val viewModel: MainViewModel by viewModel()

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
        val lang = storageManager.currentLanguage
        super.attachBaseContext(localeManager.setLocale(base, lang))
    }
}

