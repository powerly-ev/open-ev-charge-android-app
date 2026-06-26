package com.powerly.e2e.fakes

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.CurrenciesStatus
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Deterministic [AppRepository] for E2E journeys: countries resolved, language "en". */
class FakeAppRepository : AppRepository {
    override suspend fun showOnBoardingOnce(): Boolean = false
    override suspend fun showRegisterNotification(): Boolean = false
    override suspend fun updateCountries(): ApiStatus<List<Country>> =
        ApiStatus.Success(listOf(TestData.country))
    override suspend fun getUserCountry(): Country = TestData.country
    override suspend fun getCountriesList(): List<Country> = listOf(TestData.country)
    override suspend fun getCountryById(id: Int): Country = TestData.country
    override suspend fun getCountryByIso(iso: String): Country = TestData.country
    override suspend fun getCurrencies(): CurrenciesStatus = CurrenciesStatus.Success(emptyList())
    override suspend fun getCountryInfo(countryId: Int): ApiStatus<Country> =
        ApiStatus.Success(TestData.country)
    override suspend fun updateAppLanguage(language: String): ApiStatus<Boolean> = ApiStatus.Success(true)
    override suspend fun setLanguage(language: String) {}
    override suspend fun getLanguage(): String = "en"
    override val languageFlow: Flow<String> = flowOf("en")
    override suspend fun updateDevice(language: String?): ApiStatus<Boolean> = ApiStatus.Success(true)
}
