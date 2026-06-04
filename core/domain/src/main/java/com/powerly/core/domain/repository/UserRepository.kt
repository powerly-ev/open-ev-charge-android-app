package com.powerly.core.domain.repository

import com.powerly.core.domain.model.AuthStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository {

    val userFlow: Flow<User?>

    val loggedInFlow: Flow<Boolean>

    val currencyFlow: Flow<String>

    val languageFlow: Flow<String>

    suspend fun isLoggedIn(): Boolean

    suspend fun getCurrency(): String

    suspend fun getLanguage(): String

    suspend fun updateUserDetails(request: UserUpdateBody): ApiStatus<User>

    suspend fun getUserDetails(): ApiStatus<User>

    suspend fun updateLocallBalance(balance: Double)

    suspend fun refreshToken(): ApiStatus<String>

    suspend fun checkAuthentication(): AuthStatus

    suspend fun logout(): ApiStatus<Boolean>

    /** Emits when the user is signed out, so observers can react (e.g. navigate). */
    val logoutEvent: SharedFlow<Boolean>

    suspend fun deleteUser(): ApiStatus<Boolean>

    suspend fun clearLoginData()
}
