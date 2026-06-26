package com.powerly.core.data.repository

import com.powerly.core.data.datasource.remote.AppRemoteDataSource
import com.powerly.core.data.datasource.remote.UserRemoteDataSource
import com.powerly.core.database.LocalDataSource
import com.powerly.core.database.StorageManager
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.model.user.User
import com.powerly.core.network.DeviceHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppRepositoryImplTest {

    private val appRemoteDataSource = mockk<AppRemoteDataSource>()
    private val userRemoteDataSource = mockk<UserRemoteDataSource>()
    private val localDataSource = mockk<LocalDataSource>(relaxed = true)
    private val storageManager = mockk<StorageManager>(relaxed = true)
    private val deviceHelper = mockk<DeviceHelper>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: AppRepositoryImpl

    private val egypt = Country(id = 1, name = "Egypt", iso = "EG")

    @Before
    fun setUp() {
        repository = AppRepositoryImpl(
            appRemoteDataSource = appRemoteDataSource,
            userRemoteDataSource = userRemoteDataSource,
            localDataSource = localDataSource,
            storageManager = storageManager,
            deviceHelper = deviceHelper,
            ioDispatcher = dispatcher,
        )
    }

    // ---- onboarding / register notification flags --------------------------------------------

    @Test
    fun `showOnBoardingOnce returns true and disables the flag`() = runTest {
        coEvery { storageManager.shouldShowOnBoarding() } returns true

        assertTrue(repository.showOnBoardingOnce())
        coVerify(exactly = 1) { storageManager.setShowOnBoarding(false) }
    }

    @Test
    fun `showOnBoardingOnce returns false without touching the flag`() = runTest {
        coEvery { storageManager.shouldShowOnBoarding() } returns false

        assertFalse(repository.showOnBoardingOnce())
        coVerify(exactly = 0) { storageManager.setShowOnBoarding(any()) }
    }

    // ---- updateCountries: cache-then-network -------------------------------------------------

    @Test
    fun `updateCountries returns cached data without calling the network`() = runTest {
        coEvery { localDataSource.getCountries() } returns listOf(egypt)

        val result = repository.updateCountries()

        assertTrue(result is ApiStatus.Success)
        assertEquals(listOf(egypt), (result as ApiStatus.Success).data)
        coVerify(exactly = 0) { appRemoteDataSource.getCountries() }
    }

    @Test
    fun `updateCountries fetches and caches when there is no local data`() = runTest {
        coEvery { localDataSource.getCountries() } returns emptyList()
        coEvery { appRemoteDataSource.getCountries() } returns ApiStatus.Success(listOf(egypt))

        val result = repository.updateCountries()

        assertEquals(listOf(egypt), (result as ApiStatus.Success).data)
        coVerify(exactly = 1) { localDataSource.insertCountries(listOf(egypt)) }
    }

    @Test
    fun `updateCountries does not cache when the network fails`() = runTest {
        coEvery { localDataSource.getCountries() } returns emptyList()
        coEvery { appRemoteDataSource.getCountries() } returns ApiStatus.Error("down".asErrorMessage)

        val result = repository.updateCountries()

        assertTrue(result is ApiStatus.Error)
        coVerify(exactly = 0) { localDataSource.insertCountries(any()) }
    }

    // ---- getCountriesList --------------------------------------------------------------------

    @Test
    fun `getCountriesList returns local countries when present`() = runTest {
        coEvery { localDataSource.getCountries() } returns listOf(egypt)

        assertEquals(listOf(egypt), repository.getCountriesList())
        coVerify(exactly = 0) { appRemoteDataSource.getCountries() }
    }

    @Test
    fun `getCountriesList falls back to the network when local is empty`() = runTest {
        coEvery { localDataSource.getCountries() } returns emptyList()
        coEvery { appRemoteDataSource.getCountries() } returns ApiStatus.Success(listOf(egypt))

        assertEquals(listOf(egypt), repository.getCountriesList())
    }

    // ---- getUserCountry ----------------------------------------------------------------------

    @Test
    fun `getUserCountry returns null when no country id is stored`() = runTest {
        coEvery { storageManager.getCountryId() } returns null

        assertNull(repository.getUserCountry())
    }

    @Test
    fun `getUserCountry resolves the stored country id`() = runTest {
        coEvery { storageManager.getCountryId() } returns 1
        coEvery { localDataSource.getCountryById(1) } returns egypt

        assertEquals(egypt, repository.getUserCountry())
    }

    // ---- updateAppLanguage -------------------------------------------------------------------

    @Test
    fun `updateAppLanguage saves the user and clears country on success`() = runTest {
        val user = User(id = 1, firstName = "Jane")
        coEvery { storageManager.getCurrentLanguage() } returns "en"
        coEvery { appRemoteDataSource.updateDevice(any()) } returns ApiStatus.Success(true)
        coEvery { userRemoteDataSource.getUser() } returns ApiStatus.Success(user)

        val result = repository.updateAppLanguage("ar")

        assertTrue(result is ApiStatus.Success && result.data)
        coVerify { storageManager.setCurrentLanguage("ar") }
        coVerify { storageManager.saveUser(user) }
        coVerify { storageManager.clearCountry() }
    }

    @Test
    fun `updateAppLanguage rolls back the language when the user refresh fails`() = runTest {
        coEvery { storageManager.getCurrentLanguage() } returns "en"
        coEvery { appRemoteDataSource.updateDevice(any()) } returns ApiStatus.Success(true)
        coEvery { userRemoteDataSource.getUser() } returns ApiStatus.Error("boom".asErrorMessage)

        val result = repository.updateAppLanguage("ar")

        assertTrue(result is ApiStatus.Error)
        coVerifyOrder {
            storageManager.setCurrentLanguage("ar")
            storageManager.setCurrentLanguage("en")
        }
    }
}
