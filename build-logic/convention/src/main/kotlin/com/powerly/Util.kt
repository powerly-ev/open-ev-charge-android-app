package com.powerly

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.gradle.api.invocation.Gradle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Determines the build variant name from a list of potential names.
 *
 * This function prioritizes variant keywords in the following order: "demo", "debug", "release", "benchmark".
 * Returns the first matching keyword found in the input `names`. If no keywords are found, returns an empty string.
 *
 * @param names A list of strings, each potentially containing a variant keyword.
 * @return The variant name ("demo", "debug", "release", "benchmark", or "") based on keyword presence and priority.
 * @throws IllegalArgumentException if the input list `names` is null.
 */
private fun getVariantName(names: List<String>): String {
    return when {
        names.any { it.contains("Demo") } -> "demo"
        names.any { it.contains("Debug") } -> "debug"
        names.any { it.contains("Release") } -> "release"
        names.any { it.contains("Benchmark") } -> "benchmark"
        else -> ""
    }
}

/**
 * Returns the appropriate properties file name based on the active build variant.
 *
 * Maps build variants to specific properties files:
 * - "debug", "benchmark": "secrets.debug.properties"
 * - "release": "secrets.production.properties"
 * - Default (all others): "secrets.default.properties"
 *
 * Extracts the build variant from the Gradle task names.
 *
 * @param gradle The Gradle instance.
 * @return The properties file name for the current build variant.
 * @see getVariantName For how the build variant is determined.
 */
fun getPropertiesFileName(gradle: Gradle): String {
    val buildVariant = getVariantName(gradle.startParameter.taskNames)
    return when (buildVariant) {
        "demo" -> "secrets.default.properties"
        "debug", "benchmark" -> "secrets.debug.properties"
        "release" -> "secrets.production.properties"
        else -> "secrets.default.properties"
    }
}


/**
 * Renames the APK file during the build process.
 *
 * The filename pattern is: `${Project.DISPLAY_NAME}-${variantName}-${date}.apk`
 *
 * `variantName` is derived from `variantOutputImpl.name`:
 *   - "debug" becomes "test"
 *   - "preRelease" becomes "production"
 *   - Otherwise, it remains the same.
 *
 * `date` is the current date in "dd-MMM" format (e.g., 25-Oct).
 *
 * @param variantOutputImpl The [BaseVariantOutputImpl] representing the build variant output.
 */
fun appBuildName(variantOutputImpl: BaseVariantOutputImpl) {
    val variantName = when (val name = variantOutputImpl.name) {
        "debug" -> "test"
        "preRelease" -> "production"
        else -> name
    }.replace("default-", "")
    println("variantName - $variantName")
    val date = SimpleDateFormat("dd-MMM", Locale.US).format(Date()) // date Day:Month
    variantOutputImpl.outputFileName = "${MyProject.DISPLAY_NAME}-$variantName-$date.apk"
}

/**
 * Checks if the current Gradle build is an HMS (Huawei Mobile Services) build.
 *
 * It determines this by checking if the task requests contain "Hms".
 *
 * @param gradle The current Gradle instance.
 * @return `true` if the build is an HMS build, `false` otherwise.
 */
fun isHuawei(gradle: Gradle): Boolean {
    return gradle.startParameter.taskRequests.toString().contains("Hms")
}

/**
 * Checks if the current Gradle build is an GMS (Google Pay Services) build.
 *
 * It determines this by checking if the task requests contain "Gms".
 *
 * @param gradle The current Gradle instance.
 * @return `true` if the build is an GMS build, `false` otherwise.
 */
fun isGoogle(gradle: Gradle): Boolean {
    return gradle.startParameter.taskRequests.toString().contains("Gms")
}