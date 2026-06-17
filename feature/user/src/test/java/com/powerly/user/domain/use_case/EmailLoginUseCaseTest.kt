package com.powerly.user.domain.use_case

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.Message
import com.powerly.core.domain.model.user.User
import com.powerly.core.network.api.ApiErrorConstants
import com.powerly.user.domain.model.LoginResult
import com.powerly.user.domain.model.UserVerification
import com.powerly.user.domain.repository.LoginEmailRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmailLoginUseCaseTest {

    private val repository = mockk<LoginEmailRepository>()
    private val useCase = EmailLoginUseCase(repository)

    @Test
    fun `successful login yields SUCCESS with no error`() = runTest {
        coEvery { repository.emailLogin("e@x.com", "pw") } returns ApiStatus.Success(User(id = 1))

        val result = useCase("e@x.com", "pw")

        assertEquals(LoginResult.SUCCESS, result.outcome)
        assertNull(result.error)
        coVerify(exactly = 0) { repository.emailVerifyResend(any()) }
    }

    @Test
    fun `non-auth error yields ERROR and does not resend`() = runTest {
        val msg = Message("server", Message.ERROR, ApiErrorConstants.SYSTEM_ERROR)
        coEvery { repository.emailLogin(any(), any()) } returns ApiStatus.Error(msg)

        val result = useCase("e@x.com", "pw")

        assertEquals(LoginResult.ERROR, result.outcome)
        assertEquals(msg, result.error)
        coVerify(exactly = 0) { repository.emailVerifyResend(any()) }
    }

    @Test
    fun `unauthenticated triggers resend and yields VERIFICATION_REQUIRED`() = runTest {
        val loginMsg = Message("unverified", Message.ERROR, ApiErrorConstants.UNAUTHENTICATED)
        coEvery { repository.emailLogin(any(), any()) } returns ApiStatus.Error(loginMsg)
        coEvery { repository.emailVerifyResend("e@x.com") } returns
            ApiStatus.Success(mockk<UserVerification>(relaxed = true))

        val result = useCase("e@x.com", "pw")

        assertEquals(LoginResult.VERIFICATION_REQUIRED, result.outcome)
        assertEquals(loginMsg, result.error)
        coVerify(exactly = 1) { repository.emailVerifyResend("e@x.com") }
    }

    @Test
    fun `unauthenticated with failed resend yields ERROR carrying the resend message`() = runTest {
        val loginMsg = Message("unverified", Message.ERROR, ApiErrorConstants.UNAUTHENTICATED)
        val resendMsg = Message("too many", Message.ERROR, ApiErrorConstants.TOO_MANY_REQUESTS)
        coEvery { repository.emailLogin(any(), any()) } returns ApiStatus.Error(loginMsg)
        coEvery { repository.emailVerifyResend(any()) } returns ApiStatus.Error(resendMsg)

        val result = useCase("e@x.com", "pw")

        assertEquals(LoginResult.ERROR, result.outcome)
        assertEquals(resendMsg, result.error)
    }
}
