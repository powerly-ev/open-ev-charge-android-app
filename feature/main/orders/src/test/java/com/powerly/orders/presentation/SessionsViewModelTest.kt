package com.powerly.orders.presentation

import com.powerly.core.domain.model.powerly.Session
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.domain.manager.PusherManager
import com.powerly.orders.domain.repository.SessionsRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SessionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sessionsRepository = mockk<SessionsRepository>()
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val pusherManager = mockk<PusherManager>(relaxed = true)

    private fun viewModel(): SessionsViewModel {
        every { sessionsRepository.activeOrders } returns emptyFlow()
        every { sessionsRepository.completedOrders } returns emptyFlow()
        every { pusherManager.sessionCompletionFlow } returns MutableStateFlow(null)
        return SessionsViewModel(sessionsRepository, userRepository, pusherManager)
    }

    @Test
    fun `setSession selects the session and marks it delivered`() = runTest {
        val viewModel = viewModel()

        viewModel.setSession(Session(id = "s1"))

        assertEquals("s1", viewModel.selectedSession.value?.id)
        assertEquals(2, viewModel.selectedSession.value?.status)
    }

    @Test
    fun `refreshCompletedOrders flips the refresh flag`() = runTest {
        val viewModel = viewModel()
        assertFalse(viewModel.refresh.value)

        viewModel.refreshCompletedOrders()

        assertTrue(viewModel.refresh.value)
    }
}
