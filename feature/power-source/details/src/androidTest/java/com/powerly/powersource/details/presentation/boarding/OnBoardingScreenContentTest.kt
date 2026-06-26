package com.powerly.powersource.details.presentation.boarding

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.powerly.resources.R
import com.powerly.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI test for [OnBoardingScreenContent]: verifies the skip/next controls
 * render and that tapping "skip" invokes onDone.
 */
@RunWith(AndroidJUnit4::class)
class OnBoardingScreenContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(id: Int): String = composeTestRule.activity.getString(id)

    @Test
    fun shows_next_and_skip_and_fires_done_on_skip() {
        var done = false

        composeTestRule.setContent {
            AppTheme {
                OnBoardingScreenContent(onDone = { done = true })
            }
        }

        composeTestRule.onNodeWithText(string(R.string.next)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.skip)).performClick()
        assertTrue("onDone should fire when skipping", done)
    }
}
