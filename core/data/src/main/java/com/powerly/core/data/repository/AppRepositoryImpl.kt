package com.powerly.core.data.repository

import com.powerly.core.data.datasource.remote.AppRemoteDataSource
import com.powerly.core.data.datasource.remote.UserRemoteDataSource
import com.powerly.core.database.LocalDataSource
import com.powerly.core.database.StorageManager
import com.powerly.core.domain.model.CurrenciesStatus
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.location.Country
import com.powerly.core.data.model.DeviceBody
import com.powerly.core.network.DeviceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class AppRepositoryImpl(
    private val appRemoteDataSource: AppRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val storageManager: StorageManager,
    private val deviceHelper: DeviceHelper,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : AppRepository {

    override suspend fun showOnBoardingOnce(): Boolean {
        val show = storageManager.shouldShowOnBoarding()
        if (show) storageManager.setShowOnBoarding(false)
        return show
    }

    override suspend fun showRegisterNotification(): Boolean {
        return if (storageManager.shouldShowRegisterNotification()) {
            storageManager.setShowRegisterNotification(false)
            true
        } else false
    }

    override suspend fun updateCountries() = withContext(ioDispatcher) {
        val local = localDataSource.getCountries().orEmpty()
        if (local.isNotEmpty()) {
            ApiStatus.Success(local)
        } else {
            val result = appRemoteDataSource.getCountries()
            if (result is ApiStatus.Success) localDataSource.insertCountries(result.data)
            result
        }
    }

    override suspend fun getCountriesList(): List<Country> {
        val countries = localDataSource.getCountries().orEmpty()
        return countries.ifEmpty {
            when (val it = updateCountries()) {
                is ApiStatus.Success -> it.data
                else -> emptyList()
            }
        }
    }

    override suspend fun getCountryByIso(iso: String): Country? =
        localDataSource.getCountryByIso(iso)

    override suspend fun getCountryById(id: Int): Country? =
        localDataSource.getCountryById(id)

    override suspend fun getUserCountry(): Country? {
        val countryId = storageManager.getCountryId() ?: return null
        return getCountryById(countryId)
    }

    override suspend fun getCurrencies(): CurrenciesStatus = withContext(ioDispatcher) {
        appRemoteDataSource.getCurrencies()
    }

    override suspend fun getCountryInfo(countryId: Int) = withContext(ioDispatcher) {
        appRemoteDataSource.getCountry(countryId)
    }

    override suspend fun updateDevice(language: String?) = withContext(ioDispatcher) {
        val body = DeviceBody(
            lang = language ?: storageManager.getCurrentLanguage(),
            deviceImei = storageManager.getUniqueId(),
            deviceToken = storageManager.getMessagingToken().ifEmpty { null },
            appVersion = deviceHelper.appVersion,
            deviceModel = deviceHelper.deviceModel,
            deviceVersion = deviceHelper.deviceVersion,
            deiceType = deviceHelper.deviceType
        )
        appRemoteDataSource.updateDevice(body)
    }

    override suspend fun updateAppLanguage(language: String) = withContext(ioDispatcher) {
        val oldLanguage = storageManager.getCurrentLanguage()
        storageManager.setCurrentLanguage(language)
        updateDevice(language)
        when (val userResult = userRemoteDataSource.getUser()) {
            is ApiStatus.Success -> {
                storageManager.saveUser(userResult.data)
                storageManager.clearCountry()
                ApiStatus.Success(true)
            }
            is ApiStatus.Error -> {
                storageManager.setCurrentLanguage(oldLanguage)
                userResult
            }
            is ApiStatus.Loading -> userResult
        }
    }

    override suspend fun setLanguage(language: String) {
        storageManager.setCurrentLanguage(language)
    }

    override suspend fun getLanguage(): String = storageManager.getCurrentLanguage()

    override val languageFlow = storageManager.languageFlow
}
