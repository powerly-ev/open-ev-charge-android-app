package com.powerly.user.email

internal const val PASSWORD_MIN_LENGTH = 8

/**
 * Checks the strength of a given password.
 *
 * @param password The password to check.
 * @return The strength of the password as a [PasswordStrength] enum value.
 */
internal fun checkPasswordStrength(password: String): PasswordStrength {
    // Criteria for password strength
    val minLength = PASSWORD_MIN_LENGTH
    val mediumLength = 12

    // Regular expressions for various character sets
    val uppercaseRegex = Regex("[A-Z]+")
    val lowercaseRegex = Regex("[a-z]+")
    val numberRegex = Regex("[0-9]+")
    val specialCharRegex = Regex("[!@#$%^&*(),.?\":{}|<>]+")

    // Evaluate password
    val lengthScore = if (password.length >= minLength) 1 else 0
    val mediumLengthScore = if (password.length >= mediumLength) 1 else 0
    val uppercaseScore = if (uppercaseRegex.containsMatchIn(password)) 1 else 0
    val lowercaseScore = if (lowercaseRegex.containsMatchIn(password)) 1 else 0
    val numberScore = if (numberRegex.containsMatchIn(password)) 1 else 0
    val specialCharScore = if (specialCharRegex.containsMatchIn(password)) 1 else 0
    val score =
        lengthScore + mediumLengthScore + uppercaseScore + lowercaseScore + numberScore + specialCharScore

    // Determine password strength based on score
    return when (score) {
        in 0..2 -> PasswordStrength.WEAK
        in 3..4 -> PasswordStrength.MEDIUM
        in 5..6 -> PasswordStrength.STRONG
        else -> PasswordStrength.WEAK
    }
}

internal enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG
}