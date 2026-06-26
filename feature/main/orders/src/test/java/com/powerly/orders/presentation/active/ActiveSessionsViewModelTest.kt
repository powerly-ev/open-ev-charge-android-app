package com.powerly.orders.presentation.active

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.PowerSource
import com.powerly.core.domain.model.powerly.Session
import com.powerly.orders.domain.repository.SessionsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveSessionsViewModelTest {

    private val sessionsRepository = mockk<SessionsRepository>()
    private val viewModel = ActiveSessionsViewModel(sessionsRepository)

    @Test
    fun `stopCharging returns null for a null session`() = runTest {
        assertNull(viewModel.stopCharging(null))
    }

    @Test
    fun `stopCharging forwards the session identifiers and returns the result`() = runTest {
        val session = Session(id = "s1", connectorNumber = 1, chargePoint = PowerSource(id = "cp1"))
        coEvery {
            sessionsRepository.stopCharging(orderId = "s1", chargePointId = "cp1", connector = 1)
        } returns ApiStatus.Success(session)

        val result = viewModel.stopCharging(session)

        assertTrue(result is ApiStatus.Success)
        coVerify(exactly = 1) {
            sessionsRepository.stopCharging(orderId = "s1", chargePointId = "cp1", connector = 1)
        }
    }
}
