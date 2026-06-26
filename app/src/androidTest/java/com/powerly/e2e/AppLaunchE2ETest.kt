package com.powerly.e2e

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.powerly.MainActivity
import com.powerly.navigation.MainScreen.setMainScreenWelcome
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke end-to-end test: launches the full app (real Koin graph with fake repositories/managers
 * from [com.powerly.TestApp]) at the Welcome flow and verifies it boots and composes a UI tree.
 *
 * Launched at Welcome (not Splash) to avoid the Splash GIF, which never lets the Compose test
 * clock idle.
 */
@RunWith(AndroidJUnit4::class)
class AppLaunchE2ETest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun app_launches_and_renders() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setMainScreenWelcome()
        }
        ActivityScenario.launch<MainActivity>(intent)

        composeTestRule.waitUntil(10_000) {
            try {
                composeTestRule.onAllNodes(isRoot()).fetchSemanticsNodes().isNotEmpty()
            } catch (e: IllegalStateException) {
                false
            }
        }
    }
}
