package com.powerly.payment.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.payment.data.datasource.remote.PaymentRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

/** [CardsRepositoryImpl] is a thin delegate; tests pin that calls forward their arguments. */
class CardsRepositoryImplTest {

    private val remoteDataSource = mockk<PaymentRemoteDataSource>()
    private val repository = CardsRepositoryImpl(remoteDataSource, UnconfinedTestDispatcher())

    @Test
    fun `addCard forwards the token`() = runTest {
        coEvery { remoteDataSource.cardAdd("tok") } returns ApiStatus.Success(true)

        assertTrue(repository.addCard("tok") is ApiStatus.Success)
        coVerify(exactly = 1) { remoteDataSource.cardAdd("tok") }
    }

    @Test
    fun `setDefaultCard forwards the id`() = runTest {
        coEvery { remoteDataSource.cardDefault("c1") } returns ApiStatus.Success(true)

        assertTrue(repository.setDefaultCard("c1") is ApiStatus.Success)
        coVerify(exactly = 1) { remoteDataSource.cardDefault("c1") }
    }

    @Test
    fun `deleteCard forwards the id`() = runTest {
        coEvery { remoteDataSource.cardDelete("c1") } returns ApiStatus.Success(true)

        assertTrue(repository.deleteCard("c1") is ApiStatus.Success)
        coVerify(exactly = 1) { remoteDataSource.cardDelete("c1") }
    }
}
