package com.example.dkalita.pocopay

import android.arch.core.executor.ArchTaskExecutor
import android.content.Context
import com.example.dkalita.pocopay.datasource.PocopayApi
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiResponse
import com.example.dkalita.pocopay.datasource.PocopayApi.Companion.ERROR_TIMEOUT
import com.example.dkalita.pocopay.datasource.PocopayApi.Companion.ERROR_WRONG_CREDENTIALS
import com.example.dkalita.pocopay.datasource.PocopayApi.LoginRequest
import com.example.dkalita.pocopay.viewmodel.LoginViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Matchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.powermock.modules.junit4.PowerMockRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import kotlin.properties.Delegates
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


@RunWith(PowerMockRunner::class)
class LoginViewModelTest {

	var loginViewModel by Delegates.notNull<LoginViewModel>()

	@Mock
	lateinit var context: Context

	@Mock
	lateinit var webService: PocopayApi

	@Mock
	lateinit var call: Call<ApiResponse>

	@Captor
	lateinit var callbackArgument: ArgumentCaptor<Callback<ApiResponse>>

	var loginCompleteCalled = false

	@Before
	fun setUp() {
		ArchTaskExecutor.getInstance().setDelegate(ImmediateExecutor())

		whenCall(context.getString(anyInt())).then { it.arguments[0].toString() }

		loginViewModel = LoginViewModel(context, webService)

		loginCompleteCalled = false
		loginViewModel.onLoginComplete.observeForever { loginCompleteCalled = true }
	}

	@Test
	fun noUsername() {
		loginViewModel.login = ""

		loginViewModel.attemptLogin()

		assertFalse(loginCompleteCalled)
		assertEquals("${R.string.error_empty_credentials}", loginViewModel.error.value)
	}

	@Test
	fun noPassword() {
		loginViewModel.login = "testUser"
		loginViewModel.password = ""

		loginViewModel.attemptLogin()

		assertFalse(loginCompleteCalled)
		assertEquals("${R.string.error_empty_credentials}", loginViewModel.error.value)
	}

	@Test
	fun loginSuccessful() {
		whenCall(webService.post(LoginRequest("testUser", "testPassword"))).thenReturn(call)

		loginViewModel.login = "testUser"
		loginViewModel.password = "testPassword"
		loginViewModel.attemptLogin()

		verify(call).enqueue(callbackArgument.capture())

		val response = Response.success(ApiResponse("success"))
		callbackArgument.value.onResponse(call, response)


		assertTrue(loginCompleteCalled)
		assertNull(loginViewModel.error.value)
	}

	@Test
	fun loginSuccessfulAfterTimeout() {
		whenCall(webService.post(LoginRequest("testUser", "testPassword"))).thenReturn(call)

		loginViewModel.login = "testUser"
		loginViewModel.password = "testPassword"
		loginViewModel.attemptLogin()

		verify(call).enqueue(callbackArgument.capture())
		Mockito.reset(call)

		val timeoutResponse = createErrorResponse(HTTP_BAD_REQUEST, ERROR_TIMEOUT)
		callbackArgument.value.onResponse(call, timeoutResponse)

		assertFalse(loginCompleteCalled)
		verify(call).enqueue(callbackArgument.capture())

		val successResponse = Response.success(ApiResponse("success"))
		callbackArgument.value.onResponse(call, successResponse)

		assertTrue(loginCompleteCalled)
		assertNull(loginViewModel.error.value)
	}

	@Test
	fun loginFailed() {
		whenCall(webService.post(LoginRequest("testUser", "testPassword"))).thenReturn(call)

		loginViewModel.login = "testUser"
		loginViewModel.password = "testPassword"
		loginViewModel.attemptLogin()

		verify(call).enqueue(callbackArgument.capture())

		callbackArgument.value.onFailure(call, RuntimeException("network error"))

		assertFalse(loginCompleteCalled)
		assertEquals("${R.string.error_unknown}", loginViewModel.error.value)
	}

	@Test
	fun loginWrongCredentials() {
		whenCall(webService.post(LoginRequest("testUser", "testPassword"))).thenReturn(call)

		loginViewModel.login = "testUser"
		loginViewModel.password = "testPassword"
		loginViewModel.attemptLogin()

		verify(call).enqueue(callbackArgument.capture())

		val response = createErrorResponse(HTTP_BAD_REQUEST, ERROR_WRONG_CREDENTIALS)
		callbackArgument.value.onResponse(call, response)

		assertFalse(loginCompleteCalled)
		assertEquals("${R.string.error_wrong_credentials}", loginViewModel.error.value)
	}

	@Test
	fun loginFailedAfterThreeTimeouts() {
		whenCall(webService.post(LoginRequest("testUser", "testPassword"))).thenReturn(call)

		loginViewModel.login = "testUser"
		loginViewModel.password = "testPassword"
		loginViewModel.attemptLogin()

		verify(call).enqueue(callbackArgument.capture())
		Mockito.reset(call)

		val timeout1Response = createErrorResponse(HTTP_BAD_REQUEST, ERROR_TIMEOUT)
		callbackArgument.value.onResponse(call, timeout1Response) // timeout 1

		verify(call).enqueue(callbackArgument.capture())
		Mockito.reset(call)

		val timeout2Response = createErrorResponse(HTTP_BAD_REQUEST, ERROR_TIMEOUT)
		callbackArgument.value.onResponse(call, timeout2Response) // timeout 2

		verify(call).enqueue(callbackArgument.capture())
		Mockito.reset(call)

		val timeout3Response = createErrorResponse(HTTP_BAD_REQUEST, ERROR_TIMEOUT)
		callbackArgument.value.onResponse(call, timeout3Response) // timeout 3

		verifyZeroInteractions(call)
		assertFalse(loginCompleteCalled)
		assertEquals("${R.string.error_timeout}", loginViewModel.error.value)
	}
}
