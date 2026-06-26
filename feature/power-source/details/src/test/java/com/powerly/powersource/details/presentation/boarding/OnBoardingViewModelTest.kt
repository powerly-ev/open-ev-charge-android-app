package com.powerly.powersource.details.presentation.boarding

import com.powerly.core.domain.repository.AppRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class OnBoardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appRepository = mockk<AppRepository>(relaxed = true)

    @Test
    fun `setBoarding marks onboarding as shown`() = runTest {
        OnBoardingViewModel(appRepository).setBoarding()

        coVerify(exactly = 1) { appRepository.showOnBoardingOnce() }
    }
}
