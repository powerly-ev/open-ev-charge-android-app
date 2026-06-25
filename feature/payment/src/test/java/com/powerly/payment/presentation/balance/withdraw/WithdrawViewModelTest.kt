package com.powerly.payment.presentation.balance.withdraw

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.Message
import com.powerly.core.domain.repository.UserRepository
import com.powerly.payment.domain.model.Wallet
import com.powerly.payment.domain.repository.BalanceRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WithdrawViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val balanceRepository = mockk<BalanceRepository>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val viewModel = WithdrawViewModel(balanceRepository, userRepository)

    @Test
    fun `loadWallets populates the wallet list on success`() = runTest {
        coEvery { balanceRepository.walletList() } returns
            ApiStatus.Success(listOf(mockk<Wallet>(), mockk<Wallet>()))

        viewModel.loadWallets()

        assertEquals(2, viewModel.wallets.size)
    }

    @Test
    fun `walletPayout emits Loading then the result`() = runTest {
        val payout = ApiStatus.Success(Message("done"))
        coEvery { balanceRepository.walletPayout() } returns payout

        viewModel.walletPayout().test {
            assertEquals(ApiStatus.Loading, awaitItem())
            assertEquals(payout, awaitItem())
            awaitComplete()
        }
    }
}
