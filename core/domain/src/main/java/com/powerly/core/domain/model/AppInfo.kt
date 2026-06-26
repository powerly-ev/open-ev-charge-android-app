package com.powerly.core.domain.model

/**
 * Read-only app + environment metadata that presentation code can consume
 * without depending on infrastructure (`core/network`'s `DeviceHelper`).
 *
 * Infrastructure that needs the full surface (api keys, base URLs, build flags,
 * etc.) keeps depending on `DeviceHelper` directly — this interface intentionally
 * exposes only what view models need.
 */
interface AppInfo {
    val appLink: String
    val appVersion: String
    val supportNumber: String
    val buildType: String
    val privacyPolicyUrl: String
    val termsAndConditionsUrl: String

    suspend fun isOnline(): Boolean
}
