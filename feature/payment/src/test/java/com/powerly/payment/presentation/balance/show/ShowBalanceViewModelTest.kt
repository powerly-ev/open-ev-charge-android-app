package com.powerly.payment.presentation.balance.show

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.repository.UserRepository
import com.powerly.payment.domain.model.BalanceItem
import com.powerly.payment.domain.use_case.GetBalanceItemsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ShowBalanceViewModelTest {

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val getBalanceItems = mockk<GetBalanceItemsUseCase>()
    private val viewModel = ShowBalanceViewModel(userRepository, getBalanceItems)

    @Test
    fun `balanceItems emits Loading then the fetched items`() = runTest {
        val items = ApiStatus.Success(listOf(mockk<BalanceItem>()))
        coEvery { getBalanceItems.invoke() } returns items

        viewModel.balanceItems.test {
            assertEquals(ApiStatus.Loading, awaitItem())
            assertEquals(items, awaitItem())
            awaitComplete()
        }
    }
}
