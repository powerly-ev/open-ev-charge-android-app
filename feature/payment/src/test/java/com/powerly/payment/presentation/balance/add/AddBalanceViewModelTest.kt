package com.powerly.payment.presentation.balance.add

import com.powerly.core.domain.model.Message
import com.powerly.core.domain.repository.UserRepository
import com.powerly.payment.PaymentManager
import com.powerly.payment.domain.model.BalanceRefillStatus
import com.powerly.payment.domain.model.StripCard
import com.powerly.payment.domain.repository.BalanceRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AddBalanceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val balanceRepository = mockk<BalanceRepository>()
    private val paymentManager = mockk<PaymentManager>(relaxed = true)
    private val viewModel = AddBalanceViewModel(userRepository, balanceRepository, paymentManager)

    private val card = mockk<StripCard> { every { id } returns "pm1" }

    @Test
    fun `refillBalance returns false on error`() = runTest {
        coEvery { balanceRepository.refillBalance(1, "pm1") } returns
            BalanceRefillStatus.Error(Message("declined", Message.ERROR))

        assertFalse(viewModel.refillBalance(offerId = 1, stripCard = card))
    }

    @Test
    fun `refillBalance updates the local balance and returns true on success`() = runTest {
        coEvery { balanceRepository.refillBalance(1, "pm1") } returns
            BalanceRefillStatus.Success(balance = 50.0, msg = Message("ok"))

        val success = viewModel.refillBalance(offerId = 1, stripCard = card)

        assertTrue(success)
        coVerify(exactly = 1) { userRepository.updateLocallBalance(50.0) }
    }
}
