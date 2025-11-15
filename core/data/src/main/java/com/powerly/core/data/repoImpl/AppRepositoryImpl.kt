package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.database.LocalDataSource
import com.powerly.core.database.StorageManager
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.location.Country
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.network.DeviceHelper
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.isSuccessful
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class AppRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
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
        try {
            val localCounties = localDataSource.getCountries().orEmpty()
            if (localCounties.isNotEmpty()) {
                ApiStatus.Success(localCounties)
            } else {
                val response = remoteDataSource.getCountries()
                if (response.hasData) {
                    val countries = response.getData()
                    localDataSource.insertCountries(countries)
                    ApiStatus.Success(countries)
                } else {
                    ApiStatus.Error(response.getMessage())
                }
            }
        } catch (e: ResponseException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage())
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

    override suspend fun getCountryByIso(iso: String): Country? {
        return localDataSource.getCountryByIso(iso)
    }

    override suspend fun getCountryById(id: Int): Country? {
        return localDataSource.getCountryById(id)
    }

    override suspend fun getUserCountry(): Country? {
        val countryId = storageManager.getCountryId() ?: return null
        return getCountryById(countryId)
    }

    override suspend fun getCurrencies() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getCurrencies()
            if (response.hasData) CurrenciesStatus.Success(response.getData())
            else CurrenciesStatus.Error(response.getMessage())
        } catch (e: ResponseException) {
            CurrenciesStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            CurrenciesStatus.Error(e.asErrorMessage())
        }
    }

    override suspend fun getCountryInfo(countryId: Int) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getCountry(countryId)
            if (response.hasData) ApiStatus.Success(response.getData())
            else ApiStatus.Error(response.getMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage())
        }
    }

    override suspend fun updateDevice(language: String?) = withContext(ioDispatcher) {
        try {
            val body = DeviceBody(
                lang = language ?: storageManager.getCurrentLanguage(),
                deviceImei = storageManager.getUniqueId(),
                deviceToken = storageManager.getMessagingToken().ifEmpty { null },
                appVersion = deviceHelper.appVersion,
                deviceModel = deviceHelper.deviceModel,
                deviceVersion = deviceHelper.deviceVersion,
                deiceType = deviceHelper.deviceType
            )
            val response = remoteDataSource.updateDevice(body)
            if (response.isSuccessful) ApiStatus.Success(true)
            else ApiStatus.Error(response.asErrorMessage())
        } catch (e: ResponseException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage())
        }
    }

    override suspend fun updateAppLanguage(language: String) = withContext(ioDispatcher) {
        val oldLanguage = storageManager.getCurrentLanguage()
        try {
            storageManager.setCurrentLanguage(language)
            val status = updateDevice(language)
            //if (status !is ApiStatus.Success) { }
            val userResponse = remoteDataSource.getUser()
            if (userResponse.hasData) {
                val user = userResponse.getData()
                storageManager.saveUser(user)
                storageManager.clearCountry()
                ApiStatus.Success(true)
            } else {
                storageManager.setCurrentLanguage(oldLanguage)
                ApiStatus.Error(userResponse.getMessage())
            }
        } catch (e: ResponseException) {
            e.printStackTrace()
            storageManager.setCurrentLanguage(oldLanguage)
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            storageManager.setCurrentLanguage(oldLanguage)
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage())
        }
    }

    override suspend fun setLanguage(language: String) {
        storageManager.setCurrentLanguage(language)
    }

    override suspend fun getLanguage(): String {
        return storageManager.getCurrentLanguage()
    }

    override val languageFlow = storageManager.languageFlow

}
