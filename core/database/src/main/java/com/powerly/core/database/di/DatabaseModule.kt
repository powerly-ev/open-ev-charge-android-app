package com.powerly.core.database.di


import android.content.Context
import androidx.room.Room
import com.powerly.core.database.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.powerly.core.database")
class DatabaseModule


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