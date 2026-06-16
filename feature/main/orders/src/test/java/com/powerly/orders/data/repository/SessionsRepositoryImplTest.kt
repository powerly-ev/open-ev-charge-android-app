package com.powerly.orders.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.Session
import com.powerly.orders.data.datasource.remote.SessionsRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class SessionsRepositoryImplTest {

    private val remoteDataSource = mockk<SessionsRemoteDataSource>()
    private val repository = SessionsRepositoryImpl(remoteDataSource, UnconfinedTestDispatcher())

    @Test
    fun `stopCharging forwards arguments and returns the remote result`() = runTest {
        val expected = ApiStatus.Success(Session(id = "s1"))
        coEvery { remoteDataSource.stopCharging("o1", "cp1", 2) } returns expected

        val result = repository.stopCharging(orderId = "o1", chargePointId = "cp1", connector = 2)

        assertSame(expected, result)
        coVerify(exactly = 1) { remoteDataSource.stopCharging("o1", "cp1", 2) }
    }
}
