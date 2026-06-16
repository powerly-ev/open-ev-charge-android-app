package com.powerly.vehicles.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.Vehicle
import com.powerly.vehicles.data.datasource.remote.VehiclesRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class VehiclesRepositoryImplTest {

    private val remoteDataSource = mockk<VehiclesRemoteDataSource>()
    private val repository = VehiclesRepositoryImpl(remoteDataSource, UnconfinedTestDispatcher())

    @Test
    fun `vehicleUpdate adds a new vehicle when the id is null`() = runTest {
        coEvery { remoteDataSource.vehicleAdd(any()) } returns ApiStatus.Success(Vehicle(id = 1))

        val result = repository.vehicleUpdate(Vehicle(id = null, title = "Tesla"))

        assertTrue(result is ApiStatus.Success && result.data)
        coVerify(exactly = 1) { remoteDataSource.vehicleAdd(any()) }
        coVerify(exactly = 0) { remoteDataSource.vehicleUpdate(any(), any()) }
    }

    @Test
    fun `vehicleUpdate updates an existing vehicle when the id is present`() = runTest {
        coEvery { remoteDataSource.vehicleUpdate(7, any()) } returns ApiStatus.Success(Vehicle(id = 7))

        val result = repository.vehicleUpdate(Vehicle(id = 7, title = "Tesla"))

        assertTrue(result is ApiStatus.Success && result.data)
        coVerify(exactly = 1) { remoteDataSource.vehicleUpdate(7, any()) }
        coVerify(exactly = 0) { remoteDataSource.vehicleAdd(any()) }
    }

    @Test
    fun `vehicleDelete forwards the id`() = runTest {
        coEvery { remoteDataSource.vehicleDelete(5) } returns ApiStatus.Success(true)

        assertTrue(repository.vehicleDelete(5) is ApiStatus.Success)
        coVerify(exactly = 1) { remoteDataSource.vehicleDelete(5) }
    }
}
