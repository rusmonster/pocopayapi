package com.example.dkalita.pocopay.datasource

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface PocopayApi {
	@POST("/login")
	fun post(@Body loginRequest: LoginRequest): Call<ApiResponse>

	@POST("/addUser")
	fun post(@Body signUpRequest: SignUpRequest): Call<ApiResponse>

	data class LoginRequest(
			val email: String,
			val password: String
	)

	data class SignUpRequest(
			val email: String,
			val password: String,
			val country: String,
			val city: String,
			val postal_code: String
	)

	data class ApiResponse(
			val data: String
	)

	data class ApiError(
			val error: String
	)

	companion object {

		const val ERROR_WRONG_CREDENTIALS = "err.wrong.credentials"

		const val ERROR_PASSWORD_TOO_SHORT = "err.password.too.short"

		const val ERROR_USER_EXISTS = "err.user.exists"

		const val ERROR_TIMEOUT = "err.timeout"

		fun create(): PocopayApi {
			return Retrofit.Builder()
					.baseUrl("https://poco-test.herokuapp.com/")
					.addConverterFactory(GsonConverterFactory.create())
					.build()
					.create(PocopayApi::class.java)
		}
	}
}
