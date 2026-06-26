package com.powerly.scan.presentation

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.SourceStatus
import com.powerly.core.domain.model.powerly.PowerSource
import com.powerly.core.domain.repository.PowerSourceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScannerViewModelTest {

    private val powerSourceRepository = mockk<PowerSourceRepository>()
    private val viewModel = ScannerViewModel(powerSourceRepository)

    @Test
    fun `powerSourceDetails emits Loading then the lookup result`() = runTest {
        val source = SourceStatus.Success(PowerSource(id = "p1"))
        coEvery { powerSourceRepository.getPowerSourceByIdentifier("ID-1") } returns source

        viewModel.powerSourceDetails("ID-1").test {
            assertEquals(ApiStatus.Loading, awaitItem())
            assertEquals(source, awaitItem())
            awaitComplete()
        }
    }
}
