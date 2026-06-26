package com.powerly.user.domain.use_case

import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.manager.CountryManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetectCountryUseCaseTest {

    private val appRepository = mockk<AppRepository>()
    private val countryManager = mockk<CountryManager>()
    private val useCase = DetectCountryUseCase(appRepository, countryManager)

    private val egypt = Country(id = 1, name = "Egypt", iso = "EG")

    @Test
    fun `combines the country list and the detected country`() = runTest {
        coEvery { appRepository.getCountriesList() } returns listOf(egypt)
        coEvery { countryManager.detectCountry() } returns egypt

        val result = useCase()

        assertEquals(listOf(egypt), result.countries)
        assertEquals(egypt, result.country)
    }

    @Test
    fun `detected country may be null`() = runTest {
        coEvery { appRepository.getCountriesList() } returns listOf(egypt)
        coEvery { countryManager.detectCountry() } returns null

        val result = useCase()

        assertEquals(listOf(egypt), result.countries)
        assertNull(result.country)
    }
}
