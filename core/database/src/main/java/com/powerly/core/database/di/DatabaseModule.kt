package com.powerly.core.database.di


import android.content.Context
import androidx.room.Room
import com.powerly.core.database.AppDatabase
import com.powerly.core.database.LocalDataSource
import com.powerly.core.database.StorageManager
import com.powerly.core.database.TokenEncryption
import com.powerly.core.database.createAppDataStore
import com.powerly.core.database.createUserDataStore
import com.powerly.core.database.dao.CountriesDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.powerly.core.database")
class DatabaseModule {
    @Single
    fun provideAppDatabase(
        applicationContext: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "powerly-database"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Single
    fun provideStorageManager(
        context: Context,
        tokenEncryption: TokenEncryption,
        localDataSource: LocalDataSource
    ): StorageManager {
        return StorageManager(
            context = context,
            tokenEncryption = tokenEncryption,
            userDataStore = createUserDataStore(context),
            appDataStore = createAppDataStore(context),
            localDataSource = localDataSource,
            ioDispatcher = Dispatchers.IO
        )
    }

    @Single
    fun provideCountriesDao(
        database: AppDatabase
    ): CountriesDao {
        return database.countriesDao()
    }
}


