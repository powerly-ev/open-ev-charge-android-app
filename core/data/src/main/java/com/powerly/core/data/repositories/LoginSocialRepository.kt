package com.powerly.core.data.repositories

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.SocialLoginBody
import com.powerly.core.model.user.User

interface LoginSocialRepository {

    /**
     * Logs in a user with Google.
     *
     * @param request The [SocialLoginBody] containing login details.
     * @return  [ApiStatus] results containing the login result.
     */
    suspend fun googleLogin(request: SocialLoginBody): ApiStatus<User>

    /**
     * Logs in a user with Huawei.
     *
     * @param request The [SocialLoginBody] containing login details.
     * @return  [ApiStatus] results containing the login result.
     */
    suspend fun huaweiLogin(request: SocialLoginBody): ApiStatus<User>
}