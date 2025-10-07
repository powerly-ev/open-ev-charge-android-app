package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.database.StorageManager
import com.powerly.core.database.LocalDataSource
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.network.DeviceHelper
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class AppRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val storageManager: StorageManager,
    private val deviceHelper: DeviceHelper,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : AppRepository {

    override fun showOnBoardingOnce(): Boolean {
        val show = storageManager.showOnBoarding
        if (show) storageManager.showOnBoarding = false
        return show
    }

    override fun showRegisterNotification(): Boolean {
        return if (storageManager.showRegisterNotification) {
            storageManager.showRegisterNotification = false
            true
        } else false
    }

    override fun getLanguage(): String {
        return storageManager.currentLanguage
    }

    override fun getCurrency(): String {
        return storageManager.currency
    }

    override suspend fun getCountries() = withContext(ioDispatcher) {
        try {
            val localCounties = localDataSource.getCountries()
            if (localCounties != null) {
                ApiStatus.Success(localCounties)
            } else {
                val response = remoteDataSource.getCountries()
                if (response.hasData) {
                    val countries = response.getData
                    localDataSource.insertCountries(countries)
                    ApiStatus.Success(countries)
                } else ApiStatus.Error(response.getMessage())
            }
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun getCurrencies() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getCurrencies()
            if (response.hasData) CurrenciesStatus.Success(response.getData)
            else CurrenciesStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            CurrenciesStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            CurrenciesStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun getCountryInfo(countryId: Int) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getCountry(countryId)
            if (response.hasData) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun updateDevice(language: String?) = withContext(ioDispatcher) {
        try {
            val body = DeviceBody(
                lang = language ?: storageManager.currentLanguage,
                deviceImei = storageManager.imei(),
                deviceToken = storageManager.messagingToken.ifEmpty { null },
                appVersion = deviceHelper.appVersion,
                deviceModel = deviceHelper.deviceModel,
                deviceVersion = deviceHelper.deviceVersion,
                deiceType = deviceHelper.deviceType
            )
            val response = remoteDataSource.updateDevice(body)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun updateAppLanguage(language: String) = withContext(ioDispatcher) {
        try {
            val oldLanguage = storageManager.currentLanguage
            storageManager.currentLanguage = language
            val status = updateDevice(language)
            //if (status !is ApiStatus.Success) { }
            val userResponse = remoteDataSource.getUser()
            if (userResponse.hasData) {
                val user = userResponse.getData
                storageManager.userDetails = user
                storageManager.currency = user.currency
                storageManager.clearCountry()
                ApiStatus.Success(true)
            } else {
                storageManager.currentLanguage = oldLanguage
                ApiStatus.Error(userResponse.getMessage())
            }
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }


}
