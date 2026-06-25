package com.powerly.payment.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.Message
import com.powerly.core.network.api.ApiResponse
import com.powerly.payment.data.datasource.remote.PaymentRemoteDataSource
import com.powerly.payment.data.model.BalanceRefillResponse
import com.powerly.payment.domain.model.BalanceRefillStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BalanceRepositoryImplTest {

    private val remoteDataSource = mockk<PaymentRemoteDataSource>()
    private val repository = BalanceRepositoryImpl(remoteDataSource, UnconfinedTestDispatcher())

    // ---- refillBalance: the three-way mapping --------------------------------------------------

    @Test
    fun `refillBalance returns Success when there is no redirect url`() = runTest {
        val response = BalanceRefillResponse(newBalance = 100.0, nextAction = null)
        coEvery { remoteDataSource.refillBalance(1, "pm") } returns
            ApiStatus.Success(response, Message("ok"))

        val result = repository.refillBalance(offerId = 1, paymentMethodId = "pm")

        assertTrue(result is BalanceRefillStatus.Success)
        assertEquals(100.0, (result as BalanceRefillStatus.Success).balance, 0.0)
    }

    @Test
    fun `refillBalance returns Authenticate when a redirect url is present`() = runTest {
        val response = mockk<BalanceRefillResponse> {
            every { newBalance } returns 50.0
            every { redirectUrl } returns "https://3ds"
        }
        coEvery { remoteDataSource.refillBalance(1, "pm") } returns
            ApiStatus.Success(response, Message("ok"))

        val result = repository.refillBalance(offerId = 1, paymentMethodId = "pm")

        assertTrue(result is BalanceRefillStatus.Authenticate)
        assertEquals("https://3ds", (result as BalanceRefillStatus.Authenticate).redirectUrl)
        assertEquals(50.0, result.balance, 0.0)
    }

    @Test
    fun `refillBalance maps a remote error`() = runTest {
        coEvery { remoteDataSource.refillBalance(1, "pm") } returns
            ApiStatus.Error(Message("bad", Message.ERROR))

        val result = repository.refillBalance(offerId = 1, paymentMethodId = "pm")

        assertTrue(result is BalanceRefillStatus.Error)
    }

    // ---- walletPayout: try/catch over a raw ApiResponse ----------------------------------------

    @Test
    fun `walletPayout returns Success when the response is successful`() = runTest {
        coEvery { remoteDataSource.walletPayout() } returns ApiResponse<Unit>(success = 1, message = "ok")

        assertTrue(repository.walletPayout() is ApiStatus.Success)
    }

    @Test
    fun `walletPayout returns Error when the response is not successful`() = runTest {
        coEvery { remoteDataSource.walletPayout() } returns ApiResponse<Unit>(success = 0, message = "no")

        assertTrue(repository.walletPayout() is ApiStatus.Error)
    }

    @Test
    fun `walletPayout maps a thrown exception to Error`() = runTest {
        coEvery { remoteDataSource.walletPayout() } throws RuntimeException("boom")

        assertTrue(repository.walletPayout() is ApiStatus.Error)
    }
}
