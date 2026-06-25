package com.powerly.user.data.repository

import app.cash.turbine.test
import com.powerly.core.database.StorageManager
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.core.domain.model.user.User
import com.powerly.user.data.datasource.remote.UserAuthRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginEmailRepositoryImplTest {

    private val remoteDataSource = mockk<UserAuthRemoteDataSource>()
    private val storageManager = mockk<StorageManager>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = LoginEmailRepositoryImpl(remoteDataSource, storageManager, dispatcher)

    @Test
    fun `emailLogin persists the session on success`() = runTest {
        val user = User(id = 1, firstName = "Jane")
        coEvery { remoteDataSource.emailLogin(any(), any(), any()) } returns ApiStatus.Success(user)

        val result = repository.emailLogin(email = "jane@x.com", password = "pw")

        assertTrue(result is ApiStatus.Success)
        coVerify { storageManager.saveLogin(user) }
    }

    @Test
    fun `emailLogin does not persist a session on error`() = runTest {
        coEvery { remoteDataSource.emailLogin(any(), any(), any()) } returns
            ApiStatus.Error("bad credentials".asErrorMessage)

        repository.emailLogin(email = "jane@x.com", password = "pw")

        coVerify(exactly = 0) { storageManager.saveLogin(any()) }
    }

    @Test
    fun `emailVerify persists the session on success`() = runTest {
        val user = User(id = 1)
        coEvery { remoteDataSource.emailVerify("123456", "jane@x.com") } returns ApiStatus.Success(user)

        repository.emailVerify(code = "123456", email = "jane@x.com")

        coVerify { storageManager.saveLogin(user) }
    }

    @Test
    fun `userFlow delegates to the storage manager`() = runTest {
        val user = User(id = 1, firstName = "Jane")
        every { storageManager.userFlow } returns flowOf(user)
        // userFlow is captured in the constructor, so build after stubbing.
        val repository = LoginEmailRepositoryImpl(remoteDataSource, storageManager, dispatcher)

        repository.userFlow.test {
            assertEquals(user, awaitItem())
            awaitComplete()
        }
    }
}
