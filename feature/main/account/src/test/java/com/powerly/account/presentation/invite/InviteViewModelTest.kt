package com.powerly.account.presentation.invite

import com.powerly.core.domain.model.AppInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class InviteViewModelTest {

    private val appInfo = mockk<AppInfo>()

    @Test
    fun `appLink comes from appInfo`() {
        every { appInfo.appLink } returns "https://invite"

        assertEquals("https://invite", InviteViewModel(appInfo).appLink)
    }
}
