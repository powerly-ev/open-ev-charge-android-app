package com.powerly.core.data.repository

import com.powerly.core.data.datasource.remote.PowerSourceRemoteDataSource
import com.powerly.core.domain.model.SourceStatus
import com.powerly.core.domain.model.SourcesStatus
import com.powerly.core.domain.model.powerly.PowerSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

/**
 * [PowerSourceRepositoryImpl] is a thin delegate to the remote data source; these tests pin
 * the contract that calls are forwarded with their arguments and results are returned as-is.
 */
class PowerSourceRepositoryImplTest {

    private val remoteDataSource = mockk<PowerSourceRemoteDataSource>()
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: PowerSourceRepositoryImpl

    @Before
    fun setUp() {
        repository = PowerSourceRepositoryImpl(
            remoteDataSource = remoteDataSource,
            ioDispatcher = dispatcher,
        )
    }

    @Test
    fun `getNearPowerSources forwards coordinates and returns the remote result`() = runTest {
        val expected = SourcesStatus.Success(listOf(PowerSource(id = "p1")))
        coEvery { remoteDataSource.getNearPowerSources(10.0, 20.0) } returns expected

        val result = repository.getNearPowerSources(latitude = 10.0, longitude = 20.0)

        assertSame(expected, result)
        coVerify(exactly = 1) { remoteDataSource.getNearPowerSources(10.0, 20.0) }
    }

    @Test
    fun `getPowerSource forwards the id and returns the remote result`() = runTest {
        val expected = SourceStatus.Success(PowerSource(id = "p1"))
        coEvery { remoteDataSource.getPowerSource("p1") } returns expected

        val result = repository.getPowerSource("p1")

        assertEquals(expected, result)
        coVerify(exactly = 1) { remoteDataSource.getPowerSource("p1") }
    }
}
