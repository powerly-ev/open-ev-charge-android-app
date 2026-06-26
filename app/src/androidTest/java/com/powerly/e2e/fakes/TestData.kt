package com.powerly.e2e.fakes

import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.model.user.User

/** Canned domain objects shared by the E2E fake repositories. */
object TestData {
    val country = Country(id = 1, name = "Egypt", iso = "EG")

    val user = User(
        id = 1,
        firstName = "Jane",
        lastName = "Doe",
        appCurrency = "USD",
        balance = 50.0,
        countryId = 1,
        email = "jane@example.com",
        emailVerified = 1,
        accessToken = "e2e-test-token",
    )
}
