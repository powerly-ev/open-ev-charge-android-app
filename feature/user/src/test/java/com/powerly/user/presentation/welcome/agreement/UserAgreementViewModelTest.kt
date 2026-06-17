package com.powerly.user.presentation.welcome.agreement

import com.powerly.core.domain.model.AppInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class UserAgreementViewModelTest {

    private val appInfo = mockk<AppInfo>()
    private val viewModel = UserAgreementViewModel(appInfo)

    @Test
    fun `type 1 returns the privacy policy url`() {
        every { appInfo.privacyPolicyUrl } returns "https://privacy"

        assertEquals("https://privacy", viewModel.getUserAgreementLink(type = 1))
    }

    @Test
    fun `other types return the terms and conditions url`() {
        every { appInfo.termsAndConditionsUrl } returns "https://terms"

        assertEquals("https://terms", viewModel.getUserAgreementLink(type = 2))
    }
}
