package com.powerly

import com.powerly.core.domain.manager.CountryManager
import com.powerly.core.domain.manager.NotificationsManager
import com.powerly.core.domain.manager.PusherManager
import com.powerly.core.domain.manager.UserLocationManager
import com.powerly.core.domain.model.AppInfo
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.managers.PlacesManager
import com.powerly.e2e.fakes.FakeAppRepository
import com.powerly.e2e.fakes.FakeLoginEmailRepository
import com.powerly.e2e.fakes.FakeUserRepository
import com.powerly.e2e.fakes.TestData
import com.powerly.payment.PaymentManager
import com.powerly.user.domain.repository.LoginEmailRepository
import com.powerly.user.reminder.ReminderManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Test [App] for end-to-end journeys. Provides fake repositories and neutralized managers
 * via [testOverrideModules], which [App] appends inside `startKoin` so the overrides win
 * before any `createdAtStart` singleton (e.g. PlacesManager) is eagerly created.
 */
class TestApp : App() {

    override fun testOverrideModules(): List<Module> = listOf(
        module {
            // --- repositories (faked at the domain interface) ---
            single<AppRepository> { FakeAppRepository() }
            single<UserRepository> { FakeUserRepository() }
            single<LoginEmailRepository> { FakeLoginEmailRepository() }
            single<AppInfo> { mockk(relaxed = true) { coEvery { isOnline() } returns true } }

            // --- managers (neutralized so the app launches cleanly) ---
            single<CountryManager> {
                mockk(relaxed = true) { coEvery { detectCountry() } returns TestData.country }
            }
            single<UserLocationManager> { mockk(relaxed = true) }
            single<NotificationsManager> { mockk(relaxed = true) }
            single<PusherManager> {
                mockk(relaxed = true) {
                    every { sessionCompletionFlow } returns MutableStateFlow(null)
                    every { sessionConsumptionFlow } returns MutableStateFlow(null)
                }
            }
            single<PaymentManager> { mockk(relaxed = true) }
            // Concrete @Single(createdAtStart) — faked to avoid Google Places init at launch.
            single<PlacesManager> { mockk(relaxed = true) }
            single<ReminderManager> { mockk(relaxed = true) }
        }
    )
}
