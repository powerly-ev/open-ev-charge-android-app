package com.powerly.e2e

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.powerly.MainActivity
import com.powerly.e2e.fakes.FakeLoginEmailRepository
import com.powerly.navigation.MainScreen.setMainScreenWelcome
import com.powerly.resources.R
import com.powerly.user.domain.repository.LoginEmailRepository
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

/**
 * E2E login journey on a real device, against fake repositories (see [com.powerly.TestApp]):
 * Welcome -> enter email -> Continue -> enter password -> sign in -> leaves the auth flow.
 *
 * The activity is launched straight at the Welcome flow (via the `main_destination` intent extra)
 * to bypass the Splash screen, whose continuously-animating GIF logo can't be synchronized by the
 * Compose test clock. Driven by visible text only (no testTags). Home is a map with no text, so
 * success is asserted by the auth screens disappearing.
 */
@RunWith(AndroidJUnit4::class)
class LoginJourneyE2ETest {

    // Empty rule: we launch MainActivity ourselves with a custom intent.
    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var scenario: ActivityScenario<MainActivity>? = null

    private fun str(id: Int): String = context.getString(id)

    private fun launchAtWelcome() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setMainScreenWelcome()
        }
        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun tearDown() {
        scenario?.close()
    }

    /** Tolerates the brief window before the Compose root attaches after ActivityScenario.launch. */
    private fun nodeCount(id: Int): Int = try {
        composeTestRule.onAllNodesWithText(str(id)).fetchSemanticsNodes().size
    } catch (e: IllegalStateException) {
        -1
    }

    private fun waitForText(id: Int, timeoutMs: Long = 10_000) {
        composeTestRule.waitUntil(timeoutMs) { nodeCount(id) > 0 }
    }

    @Test
    fun login_with_email_reaches_home() {
        // Existing-user path (independent of other tests that mutate the shared fake).
        (GlobalContext.get().get<LoginEmailRepository>() as FakeLoginEmailRepository).emailExists = 1

        launchAtWelcome()

        // Welcome screen.
        waitForText(R.string.login_option_email)
        composeTestRule.onNodeWithText(str(R.string.login_option_email)).performClick()

        // Email step.
        waitForText(R.string.login_email_enter)
        composeTestRule.onNodeWithText(str(R.string.login_email_enter)).performTextInput("jane@example.com")
        composeTestRule.onNodeWithText(str(R.string.continue_)).performClick()

        // Password step (existing user -> password enter).
        waitForText(R.string.login_email_password_enter)
        composeTestRule.onNodeWithText(str(R.string.login_email_password_enter)).performTextInput("password123")
        composeTestRule.onNodeWithText(str(R.string.login_option_title)).performClick()

        // Success: the auth flow is gone (navigated to Home).
        composeTestRule.waitUntil(10_000) { nodeCount(R.string.login_email_password_enter) == 0 }
    }
}
