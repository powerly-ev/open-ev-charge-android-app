package com.powerly.powersource.charging.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.ChargingStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.core.domain.model.powerly.Session
import com.powerly.powersource.charging.data.datasource.remote.ChargingRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargingRepositoryImplTest {

    private val remoteDataSource = mockk<ChargingRemoteDataSource>()
    private val repository = ChargingRepositoryImpl(remoteDataSource, UnconfinedTestDispatcher())

    @Test
    fun `startCharging maps a successful session to Success`() = runTest {
        val session = Session(id = "s1")
        coEvery { remoteDataSource.startCharging("cp1", "10", 1) } returns ApiStatus.Success(session)

        val result = repository.startCharging(chargePointId = "cp1", quantity = "10", connector = 1)

        assertTrue(result is ChargingStatus.Success)
        assertEquals(session, (result as ChargingStatus.Success).session)
    }

    @Test
    fun `startCharging maps an error to Error`() = runTest {
        coEvery { remoteDataSource.startCharging(any(), any(), any()) } returns
            ApiStatus.Error("nope".asErrorMessage)

        val result = repository.startCharging(chargePointId = "cp1", quantity = "10", connector = 1)

        assertTrue(result is ChargingStatus.Error)
    }

    @Test
    fun `stopCharging maps a successful session to Stop`() = runTest {
        val session = Session(id = "s1")
        coEvery { remoteDataSource.stopCharging("o1", "cp1", 1) } returns ApiStatus.Success(session)

        val result = repository.stopCharging(orderId = "o1", chargePointId = "cp1", connector = 1)

        assertTrue(result is ChargingStatus.Stop)
        assertEquals(session, (result as ChargingStatus.Stop).session)
    }

    @Test
    fun `sessionDetails maps a successful session to Success`() = runTest {
        coEvery { remoteDataSource.sessionDetails("o1") } returns ApiStatus.Success(Session(id = "s1"))

        assertTrue(repository.sessionDetails("o1") is ChargingStatus.Success)
    }
}
