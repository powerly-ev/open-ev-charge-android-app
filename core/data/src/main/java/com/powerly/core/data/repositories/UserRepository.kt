package com.powerly.core.data.repositories

import com.powerly.core.data.model.AuthStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody

interface UserRepository {

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
     *
     * @param imei The IMEI of the device.
     * @return  [ApiStatus] results.
     */
    suspend fun logout(imei: String): ApiStatus<Boolean>

    /**
     * Deletes a user.
     *
     * @return  [ApiStatus] results.
     */
    suspend fun deleteUser(): ApiStatus<Boolean>
}

