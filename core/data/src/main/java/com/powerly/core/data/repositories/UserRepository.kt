package com.powerly.core.data.repositories

import com.powerly.core.data.model.AuthStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    /**
     * A boolean flag indicating whether the user is currently logged in.
     *
     * @return `true` if the user is logged in, `false` otherwise.
     */
    val isLoggedIn: Boolean

    /**
     * A flow that emits the current user object.
     * It will emit a new [User] object whenever the user data changes.
     * If no user is logged in, it will emit `null`.
     */
    val userFlow: Flow<User?>

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

