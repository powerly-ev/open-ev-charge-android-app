package com.powerly.user.presentation.email

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.asErrorMessage
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.model.user.User
import com.powerly.testing.MainDispatcherRule
import com.powerly.user.domain.model.EmailCheck
import com.powerly.user.domain.model.LoginResult
import com.powerly.user.domain.model.UserVerification
import com.powerly.user.domain.repository.LoginEmailRepository
import com.powerly.user.domain.use_case.EmailLoginUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EmailLoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginRepository = mockk<LoginEmailRepository>(relaxed = true)
    private val emailLoginUseCase = mockk<EmailLoginUseCase>()
    private val viewModel = EmailLoginViewModel(loginRepository, emailLoginUseCase)

    @Test
    fun `checkEmail returns the new-user flag on success`() = runTest {
        coEvery { loginRepository.emailCheck("e@x.com") } returns
            ApiStatus.Success(mockk<EmailCheck> { every { newUser } returns true })

        assertTrue(viewModel.checkEmail("e@x.com"))
    }

    @Test
    fun `checkEmail returns false on error`() = runTest {
        coEvery { loginRepository.emailCheck(any()) } returns ApiStatus.Error("no".asErrorMessage)

        assertFalse(viewModel.checkEmail("e@x.com"))
    }

    @Test
    fun `emailLogin returns the use-case outcome`() = runTest {
        coEvery { emailLoginUseCase.invoke("e@x.com", "pw") } returns
            EmailLoginUseCase.Result(LoginResult.SUCCESS)

        assertEquals(LoginResult.SUCCESS, viewModel.emailLogin("e@x.com", "pw"))
    }

    @Test
    fun `emailLogin stores the email when verification is required`() = runTest {
        coEvery { emailLoginUseCase.invoke(any(), any()) } returns
            EmailLoginUseCase.Result(LoginResult.VERIFICATION_REQUIRED, "verify".asErrorMessage)

        val outcome = viewModel.emailLogin("e@x.com", "pw")

        assertEquals(LoginResult.VERIFICATION_REQUIRED, outcome)
        assertEquals("e@x.com", viewModel.email.value)
    }

    @Test
    fun `register returns true on success and false on error`() = runTest {
        coEvery { loginRepository.emailRegister(any(), any(), any()) } returns ApiStatus.Success(User(id = 1))
        assertTrue(viewModel.register(Country(id = 1)))

        coEvery { loginRepository.emailRegister(any(), any(), any()) } returns ApiStatus.Error("x".asErrorMessage)
        assertFalse(viewModel.register(Country(id = 1)))
    }

    @Test
    fun `emailVerify toggles resetPin and returns false on error`() = runTest {
        coEvery { loginRepository.emailVerify(any(), any()) } returns ApiStatus.Error("x".asErrorMessage)
        val before = viewModel.resetPin.value

        assertFalse(viewModel.emailVerify("1234"))
        assertEquals(!before, viewModel.resetPin.value)
    }

    @Test
    fun `forgetPassword returns the resend window on success`() = runTest {
        coEvery { loginRepository.emailForgetPassword("e@x.com") } returns
            ApiStatus.Success(mockk<UserVerification> { every { canResendInSeconds } returns 30 })

        assertEquals(30, viewModel.forgetPassword("e@x.com"))
    }

    @Test
    fun `resetPassword returns true on success`() = runTest {
        coEvery { loginRepository.emailResetPassword(any(), any(), any()) } returns ApiStatus.Success(true)

        assertTrue(viewModel.resetPassword("1234", "newpw"))
    }
}
