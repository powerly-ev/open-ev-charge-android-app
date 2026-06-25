package com.powerly.account.presentation.invite

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
 * Compose UI test for the stateless [InviteScreenContent]: renders it with fake
 * state and asserts what's shown plus the share/copy callbacks.
 */
@RunWith(AndroidJUnit4::class)
class InviteScreenContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(id: Int): String = composeTestRule.activity.getString(id)

    @Test
    fun shows_title_and_fires_share_and_copy_callbacks() {
        var shared = false
        var copied = false

        composeTestRule.setContent {
            AppTheme {
                InviteScreenContent(
                    qrDrawable = { null },
                    onCopyLink = { copied = true },
                    onShare = { shared = true },
                    onClose = {},
                )
            }
        }

        composeTestRule.onNodeWithText(string(R.string.invite)).assertIsDisplayed()

        composeTestRule.onNodeWithText(string(R.string.share_code)).performClick()
        assertTrue("onShare should fire", shared)

        composeTestRule.onNodeWithText(string(R.string.copy_code)).performClick()
        assertTrue("onCopyLink should fire", copied)
    }
}
