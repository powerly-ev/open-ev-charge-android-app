package com.powerly.core.data.repository

import app.cash.turbine.test
import com.powerly.core.data.datasource.remote.UserRemoteDataSource
import com.powerly.core.database.StorageManager
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.core.domain.model.user.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private val remoteDataSource = mockk<UserRemoteDataSource>()
    private val storageManager = mockk<StorageManager>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        repository = UserRepositoryImpl(
            remoteDataSource = remoteDataSource,
            storageManager = storageManager,
            ioDispatcher = dispatcher,
        )
    }

    // ---- flow delegation ---------------------------------------------------------------------

    // The repository captures the storage flows in its constructor, so these tests stub the
    // flow before building a fresh instance.

    @Test
    fun `userFlow delegates to the storage manager`() = runTest {
        val user = User(id = 1, firstName = "Jane")
        every { storageManager.userFlow } returns flowOf(user)
        val repository = UserRepositoryImpl(remoteDataSource, storageManager, dispatcher)

        repository.userFlow.test {
            assertEquals(user, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `loggedInFlow delegates to the storage manager`() = runTest {
        every { storageManager.loggedInFlow } returns flowOf(true)
        val repository = UserRepositoryImpl(remoteDataSource, storageManager, dispatcher)

        repository.loggedInFlow.test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    // ---- updateLocallBalance -----------------------------------------------------------------

    @Test
    fun `updateLocallBalance mutates and saves the stored user`() = runTest {
        val user = User(id = 1, balance = 0.0)
        coEvery { storageManager.getUser() } returns user
        val saved = slot<User>()

        repository.updateLocallBalance(50.0)

        coVerify { storageManager.saveUser(capture(saved)) }
        assertEquals(50.0, saved.captured.balance, 0.0)
    }

    @Test
    fun `updateLocallBalance is a no-op when there is no stored user`() = runTest {
        coEvery { storageManager.getUser() } returns null

        repository.updateLocallBalance(50.0)

        coVerify(exactly = 0) { storageManager.saveUser(any()) }
    }

    // ---- updateUserDetails -------------------------------------------------------------------

    @Test
    fun `updateUserDetails saves user and pins currency when a currency is supplied`() = runTest {
        val user = User(id = 1, appCurrency = "USD")
        coEvery {
            remoteDataSource.updateUser(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns ApiStatus.Success(user)

        repository.updateUserDetails(currency = "USD")

        coVerify { storageManager.saveUser(user) }
        coVerify { storageManager.setPinUserCurrency(true) }
    }

    @Test
    fun `updateUserDetails does not pin currency when none is supplied`() = runTest {
        val user = User(id = 1)
        coEvery {
            remoteDataSource.updateUser(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns ApiStatus.Success(user)

        repository.updateUserDetails(firstName = "Jane")

        coVerify { storageManager.saveUser(user) }
        coVerify(exactly = 0) { storageManager.setPinUserCurrency(any()) }
    }

    @Test
    fun `updateUserDetails does not save when the remote call fails`() = runTest {
        coEvery {
            remoteDataSource.updateUser(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns ApiStatus.Error("nope".asErrorMessage)

        repository.updateUserDetails(firstName = "Jane")

        coVerify(exactly = 0) { storageManager.saveUser(any()) }
    }

    // ---- getUserDetails ----------------------------------------------------------------------

    @Test
    fun `getUserDetails caches the user on success`() = runTest {
        val user = User(id = 1, firstName = "Jane")
        coEvery { remoteDataSource.getUser() } returns ApiStatus.Success(user)

        val result = repository.getUserDetails()

        assertTrue(result is ApiStatus.Success)
        coVerify { storageManager.saveUser(user) }
    }

    // ---- logout / deleteUser -----------------------------------------------------------------

    @Test
    fun `logout emits the logout event and clears login data on success`() = runTest {
        coEvery { remoteDataSource.logout(any()) } returns ApiStatus.Success(true)

        repository.logoutEvent.test {
            val result = repository.logout()

            assertTrue(result is ApiStatus.Success)
            assertTrue(awaitItem())
        }
        coVerify { storageManager.clearLoginData() }
        coVerify { storageManager.setShowRegisterNotification(true) }
    }

    @Test
    fun `logout does not clear login data when the remote call fails`() = runTest {
        coEvery { remoteDataSource.logout(any()) } returns ApiStatus.Error("fail".asErrorMessage)

        val result = repository.logout()

        assertTrue(result is ApiStatus.Error)
        coVerify(exactly = 0) { storageManager.clearLoginData() }
    }

    @Test
    fun `deleteUser clears login data on success`() = runTest {
        coEvery { remoteDataSource.deleteUser() } returns ApiStatus.Success(true)

        val result = repository.deleteUser()

        assertTrue(result is ApiStatus.Success)
        coVerify { storageManager.clearLoginData() }
        coVerify { storageManager.setShowRegisterNotification(true) }
    }
}
