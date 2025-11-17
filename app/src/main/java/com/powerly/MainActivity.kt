package com.powerly

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.powerly.lib.MainScreen.getMainDestination
import com.powerly.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModel()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val language by remember { viewModel.uiState.languageCode }
            AppTheme(language = language) {
                RootGraph(
                    startDestination = intent.getMainDestination(),
                    modifier = Modifier
                        .systemBarsPadding()
                        .imePadding(),
                )
            }
        }
        //force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        viewModel.initPaymentManager(this)
    }


    override fun attachBaseContext(base: Context) {
        val config = base.resources.configuration.apply {
            fontScale = 1f
        }
        val ctx = base.createConfigurationContext(config)
        super.attachBaseContext(ctx)
    }
}

