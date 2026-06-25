package com.powerly.payment.presentation

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.user.User
import com.powerly.core.domain.repository.UserRepository
import com.powerly.payment.domain.model.StripCard
import com.powerly.payment.domain.repository.CardsRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PaymentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val cardsRepository = mockk<CardsRepository>()

    private fun viewModel(): PaymentViewModel {
        every { userRepository.userFlow } returns emptyFlow()
        every { userRepository.logoutEvent } returns MutableSharedFlow()
        return PaymentViewModel(userRepository, cardsRepository)
    }

    private fun card(id: String, default: Boolean) = mockk<StripCard> {
        every { this@mockk.id } returns id
        every { this@mockk.default } returns default
    }

    @Test
    fun `setDefaultCard short-circuits to true for an already-default card`() = runTest {
        val viewModel = viewModel()

        assertTrue(viewModel.setDefaultCard(card(id = "c1", default = true)))
    }

    @Test
    fun `setDefaultCard updates the default on success`() = runTest {
        val card = card(id = "c1", default = false)
        coEvery { cardsRepository.setDefaultCard("c1") } returns ApiStatus.Success(true)
        coEvery { cardsRepository.cardList() } returns ApiStatus.Success(listOf(card))

        val viewModel = viewModel()

        assertTrue(viewModel.setDefaultCard(card))
        assertEquals(card, viewModel.defaultPaymentMethod.value)
    }

    @Test
    fun `loadPaymentMethods fills the methods list from the repository`() = runTest {
        coEvery { cardsRepository.cardList() } returns ApiStatus.Success(listOf(card("c1", false)))

        val viewModel = viewModel()
        viewModel.loadPaymentMethods()

        assertEquals(1, viewModel.paymentMethods.size)
    }

    @Test
    fun `deleteCard returns true on success`() = runTest {
        coEvery { cardsRepository.deleteCard("c1") } returns ApiStatus.Success(true)
        coEvery { cardsRepository.cardList() } returns ApiStatus.Success(emptyList())

        val viewModel = viewModel()

        assertTrue(viewModel.deleteCard("c1"))
    }
}
