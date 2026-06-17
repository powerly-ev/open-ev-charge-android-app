package com.powerly.vehicles.presentation.vehicleDetails.options.connectors

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.Connector
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ConnectorsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val powerSourceRepository = mockk<PowerSourceRepository>()

    @Test
    fun `connectors are fetched on init`() = runTest {
        coEvery { powerSourceRepository.connectors() } returns
            ApiStatus.Success(listOf(mockk<Connector>()))

        val viewModel = ConnectorsViewModel(powerSourceRepository)

        assertTrue(viewModel.connectors.value is ApiStatus.Success)
    }
}
