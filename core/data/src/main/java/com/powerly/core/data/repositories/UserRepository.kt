package com.powerly.core.data.repositories

import com.powerly.core.data.model.AuthStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository {
    /**
     * A flow that emits the current user object.
     * It will emit a new [User] object whenever the user data changes.
     * If no user is logged in, it will emit `null`.
     */
    val userFlow: Flow<User?>

    /**
     * A flow that emits a boolean value indicating whether the user is logged in.
     * It will emit `true` when the user logs in and `false` when they log out.
     */
    val loggedInFlow:Flow<Boolean>

    /**
     * A flow that emits the current currency.
     */
    val currencyFlow: Flow<String>

    /**
     * A flow that emits the current language.
     */
    val languageFlow: Flow<String>

    /**
     * Checks if the user is logged in.
     *
     * @return `true` if the user is logged in, `false` otherwise.
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * Retrieves the current currency.
     *
     * @return The current currency as a [String].
     */
    suspend fun getCurrency(): String

    /**
     * Retrieves the current language.
     *
     * @return The current language as a [String].
     */
    suspend fun getLanguage(): String

    /**
     * Updates user details.
     *
     * @param request The [UserUpdateBody] containing updated user details.
     * @return  [ApiStatus] results.
     */
    suspend fun updateUserDetails(request: UserUpdateBody): ApiStatus<User>

    /**
     * Retrieves user details.
     *
     * @return  [ApiStatus] results containing user details.
     */
    suspend fun getUserDetails(): ApiStatus<User>

    /**
     * Refreshes the authentication token.
     *
     * @return  [ApiStatus] results containing the new token.
     */
    suspend fun refreshToken(): ApiStatus<String>

    /**
     * Checks authentication status.
     *
     * @return  [com.powerly.core.data.model.AuthStatus] results.
     */
    suspend fun checkAuthentication(): AuthStatus

    /**
     * Logs out the user.
     * @return  [ApiStatus] results.
     */
    suspend fun logout(): ApiStatus<Boolean>

    /**
     * A shared flow that emits a [Boolean] event when the user logs out.
     */
    val logoutEvent: SharedFlow<Boolean>

    /**
     * Deletes a user.
     *
     * @return  [ApiStatus] results.
     */
    suspend fun deleteUser(): ApiStatus<Boolean>

    /**
     * Clears all locally stored login data and user information.
     *
     */
    suspend fun clearLoginData()
}

