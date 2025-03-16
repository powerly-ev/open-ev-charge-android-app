package com.powerly.core.data.di

import com.powerly.core.data.repoImpl.AppRepositoryImpl
import com.powerly.core.data.repoImpl.FeedbackRepositoryImpl
import com.powerly.core.data.repoImpl.LoginEmailRepositoryImpl
import com.powerly.core.data.repoImpl.LoginSocialRepositoryImpl
import com.powerly.core.data.repoImpl.PaymentRepositoryImpl
import com.powerly.core.data.repoImpl.PowerSourceRepositoryImpl
import com.powerly.core.data.repoImpl.SessionsRepositoryImpl
import com.powerly.core.data.repoImpl.UserRepositoryImpl
import com.powerly.core.data.repoImpl.VehiclesRepositoryImpl
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.FeedbackRepository
import com.powerly.core.data.repositories.LoginEmailRepository
import com.powerly.core.data.repositories.LoginSocialRepository
import com.powerly.core.data.repositories.PaymentRepository
import com.powerly.core.data.repositories.PowerSourceRepository
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.data.repositories.VehiclesRepository
import com.powerly.core.database.LocalDataSource
import com.powerly.core.network.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAppRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher,
    ): AppRepository = AppRepositoryImpl(
        remoteDataSource,
        localDataSource,
        ioDispatcher
    )

    @Provides
    fun provideFeedbackRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): FeedbackRepository = FeedbackRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )

    @Provides
    fun provideLoginEmailRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): LoginEmailRepository = LoginEmailRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )


    @Provides
    fun provideLoginSocialRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): LoginSocialRepository = LoginSocialRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )

    @Provides
    fun providePaymentRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): PaymentRepository = PaymentRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )

    @Provides
    fun provideSessionsRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): SessionsRepository = SessionsRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )

    @Provides
    fun provideSourceRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): PowerSourceRepository = PowerSourceRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )

    @Provides
    fun provideUserRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): UserRepository = UserRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )

    @Provides
    fun provideVehiclesRepository(
        remoteDataSource: RemoteDataSource,
        @Named("IO") ioDispatcher: CoroutineDispatcher
    ): VehiclesRepository = VehiclesRepositoryImpl(
        remoteDataSource,
        ioDispatcher
    )
}