package com.powerly.core.model.api

object ApiErrorConstants {
    const val SUCCESS_NO_CONTENT = 204
    const val UNAUTHENTICATED = 401
    const val ACCESS_DENIED = 403
    const val BANNED = 403
    const val NOT_FOUND = 404
    const val METHOD_NOT_ALLOWED = 405
    const val VALIDATION = 422
    const val UPGRADE_REQUIRED = 426
    const val TOO_MANY_REQUESTS = 429
    const val SYSTEM_ERROR = 500
    const val MAINTENANCE_MODE = 503
    const val GATEWAY_TIMEOUT = 504
    const val HOST_UNRESOLVED = 3002
}