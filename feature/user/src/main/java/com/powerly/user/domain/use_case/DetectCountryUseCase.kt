package com.powerly.user.domain.use_case

import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.model.location.Country
import com.powerly.lib.managers.CountryManager
import org.koin.core.annotation.Single

/** Loads the available country list and detects the user's current country. */
@Single
class DetectCountryUseCase(
    private val appRepository: AppRepository,
    private val countryManager: CountryManager
) {
    data class Result(val countries: List<Country>, val country: Country?)

    suspend operator fun invoke(): Result {
        val countries = appRepository.getCountriesList()
        val country = countryManager.detectCountry()
        return Result(countries, country)
    }
}
