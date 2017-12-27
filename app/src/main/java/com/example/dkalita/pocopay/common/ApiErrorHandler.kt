package com.example.dkalita.pocopay.common

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.example.dkalita.pocopay.R
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiError
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiResponse
import com.example.dkalita.pocopay.datasource.PocopayApi.Companion.ERROR_PASSWORD_TOO_SHORT
import com.example.dkalita.pocopay.datasource.PocopayApi.Companion.ERROR_TIMEOUT
import com.example.dkalita.pocopay.datasource.PocopayApi.Companion.ERROR_USER_EXISTS
import com.example.dkalita.pocopay.datasource.PocopayApi.Companion.ERROR_WRONG_CREDENTIALS
import com.google.gson.Gson
import retrofit2.Response

class ApiErrorHandler(
		private val context: Context,
		private val error: MutableLiveData<String>,
		private val retry: () -> Unit
) {
	private val MAX_TIMEOUT_RETRIEVES = 3

	private var timeoutCounter = 0

	fun resetTimeoutCounter() {
		timeoutCounter = 0
	}

	fun process(response: Response<ApiResponse>) {
		val errorBody = response.errorBody()?.string()
		val apiError = Gson().fromJson(errorBody, ApiError::class.java)

		when (apiError?.error) {
			ERROR_WRONG_CREDENTIALS ->
				error.value = context.getString(R.string.error_wrong_credentials)

			ERROR_PASSWORD_TOO_SHORT ->
				error.value = context.getString(R.string.error_password_too_short)

			ERROR_USER_EXISTS ->
				error.value = context.getString(R.string.error_user_exists)

			ERROR_TIMEOUT ->
				if (++timeoutCounter < MAX_TIMEOUT_RETRIEVES) {
					retry()
				} else {
					error.value = context.getString(R.string.error_timeout)
				}

			else ->
				error.value = context.getString(R.string.error_unknown)
		}
	}
}
