package com.example.dkalita.pocopay.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableField
import com.example.dkalita.pocopay.R
import com.example.dkalita.pocopay.common.ApiErrorHandler
import com.example.dkalita.pocopay.common.SingleLiveEvent
import com.example.dkalita.pocopay.datasource.PocopayApi
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiResponse
import com.example.dkalita.pocopay.datasource.PocopayApi.LoginRequest
import com.example.dkalita.pocopay.inject.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class LoginViewModel @Inject constructor(
		@ApplicationContext
		private val context: Context,
		private val webService: PocopayApi
) : ViewModel() {

	var login = ""

	var password = ""

	val isInProgress = ObservableField(false)

	val error = SingleLiveEvent<String>()

	val onLoginComplete = SingleLiveEvent<Unit>()

	private val errorHandler = ApiErrorHandler(context, error) { doLogin() }

	fun attemptLogin() {
		errorHandler.resetTimeoutCounter()
		doLogin()
	}

	private fun doLogin() {
		if (isInProgress.get()) {
			return
		}

		if (login.isEmpty() || password.isEmpty()) {
			error.value = context.getString(R.string.error_empty_credentials)
			return
		}

		isInProgress.set(true)

		webService.post(LoginRequest(login, password)).enqueue(object : Callback<ApiResponse> {

			override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
				isInProgress.set(false)
				error.value = context.getString(R.string.error_unknown)
			}

			override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
				isInProgress.set(false)

				if (response.isSuccessful) {
					onLoginComplete()
				} else {
					errorHandler.process(response)
				}
			}
		})
	}
}
