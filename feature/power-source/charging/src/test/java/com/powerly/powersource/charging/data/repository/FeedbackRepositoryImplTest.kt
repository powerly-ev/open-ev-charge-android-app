package com.powerly.powersource.charging.data.repository

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.core.domain.model.powerly.Session
import com.powerly.powersource.charging.data.datasource.remote.ChargingRemoteDataSource
import com.powerly.powersource.charging.domain.model.ReviewOptionsStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedbackRepositoryImplTest {

    private val remoteDataSource = mockk<ChargingRemoteDataSource>()
    private val repository = FeedbackRepositoryImpl(remoteDataSource, UnconfinedTestDispatcher())

    @Test
    fun `reviewOptions emits Success with the review reasons`() = runTest {
        val reasons = mapOf("bad" to listOf("Too slow"))
        coEvery { remoteDataSource.reviewOptions() } returns ApiStatus.Success(reasons)

        repository.reviewOptions.test {
            val item = awaitItem()
            assertTrue(item is ReviewOptionsStatus.Success)
            assertEquals(reasons, (item as ReviewOptionsStatus.Success).reasons)
            awaitComplete()
        }
    }

    @Test
    fun `reviewOptions emits Error when the remote call fails`() = runTest {
        coEvery { remoteDataSource.reviewOptions() } returns ApiStatus.Error("down".asErrorMessage)

        repository.reviewOptions.test {
            assertTrue(awaitItem() is ReviewOptionsStatus.Error)
            awaitComplete()
        }
    }

    @Test
    fun `reviewSkip forwards the order id`() = runTest {
        coEvery { remoteDataSource.reviewSkip("o1") } returns ApiStatus.Success(Session(id = "s1"))

        assertTrue(repository.reviewSkip("o1") is ApiStatus.Success)
    }
}
