package com.powerly.powersource.charging.presentation.activate

import com.powerly.core.domain.model.ChargingStatus
import com.powerly.core.domain.model.Message
import com.powerly.core.domain.model.powerly.Session
import com.powerly.powersource.charging.domain.repository.ChargingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ActivateChargerViewModelTest {

    private val chargingRepository = mockk<ChargingRepository>()
    private val viewModel = ActivateChargerViewModel(chargingRepository)

    @Test
    fun `startCharging returns the new session id on success`() = runTest {
        coEvery { chargingRepository.startCharging(any(), any(), any()) } returns
            ChargingStatus.Success(Session(id = "s1"))

        val id = viewModel.startCharging(chargePointId = "cp1", quantity = "10", connector = 1)

        assertEquals("s1", id)
    }

    @Test
    fun `startCharging returns null on error`() = runTest {
        coEvery { chargingRepository.startCharging(any(), any(), any()) } returns
            ChargingStatus.Error(Message("nope", Message.ERROR))

        val id = viewModel.startCharging(chargePointId = "cp1", quantity = "10", connector = 1)

        assertNull(id)
    }
}
