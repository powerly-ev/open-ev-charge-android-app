package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.database.LocalDataSource
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.DeviceBody
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
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : AppRepository {

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

    override suspend fun updateDevice(request: DeviceBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.updateDevice(request)
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
}
