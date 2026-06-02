package com.powerly.payment.domain.use_case

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.lib.managers.CountryManager
import com.powerly.payment.domain.repository.BalanceRepository
import org.koin.core.annotation.Single

/** Detects the user's country and fetches balance offers scoped to it. */
@Single
class GetBalanceItemsUseCase(
    private val repository: BalanceRepository,
    private val countryManager: CountryManager
) {
    suspend operator fun invoke(): ApiStatus<List<BalanceItem>> {
        val countryId = countryManager.detectCountry()?.id
        return repository.getBalanceItems(countryId)
    }
}
