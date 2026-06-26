package com.powerly.e2e.fakes

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.AuthStatus
import com.powerly.core.domain.model.user.User
import com.powerly.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Deterministic [UserRepository] for E2E. Starts logged-out (so Splash routes to
 * Welcome); all operations succeed and return [TestData.user].
 */
class FakeUserRepository : UserRepository {

    val userState = MutableStateFlow<User?>(null)
    val loggedInState = MutableStateFlow(false)

    override val userFlow = userState
    override val loggedInFlow = loggedInState
    override val currencyFlow = MutableStateFlow("USD")
    override val languageFlow = MutableStateFlow("en")
    override val logoutEvent = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    override suspend fun isLoggedIn(): Boolean = loggedInState.value
    override suspend fun getCurrency(): String = "USD"
    override suspend fun getLanguage(): String = "en"

    override suspend fun updateUserDetails(
        firstName: String?,
        lastName: String?,
        email: String?,
        password: String?,
        vatId: String?,
        countryId: Int?,
        currency: String?,
        latitude: Double?,
        longitude: Double?,
    ): ApiStatus<User> = ApiStatus.Success(TestData.user)

    override suspend fun getUserDetails(): ApiStatus<User> = ApiStatus.Success(TestData.user)
    override suspend fun updateLocallBalance(balance: Double) {}
    override suspend fun refreshToken(): ApiStatus<String> = ApiStatus.Success("e2e-test-token")
    override suspend fun checkAuthentication(): AuthStatus = AuthStatus.Success(TestData.user)
    override suspend fun logout(): ApiStatus<Boolean> = ApiStatus.Success(true)
    override suspend fun deleteUser(): ApiStatus<Boolean> = ApiStatus.Success(true)
    override suspend fun clearLoginData() {}
}
