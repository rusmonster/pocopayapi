package com.example.dkalita.pocopay.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableField
import com.example.dkalita.pocopay.R
import com.example.dkalita.pocopay.common.ApiErrorHandler
import com.example.dkalita.pocopay.common.SingleLiveEvent
import com.example.dkalita.pocopay.datasource.PocopayApi
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiResponse
import com.example.dkalita.pocopay.datasource.PocopayApi.SignUpRequest
import com.example.dkalita.pocopay.inject.ApplicationContext
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen.ABOUT
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen.ADDRESS
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen.CREDENTIALS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.properties.Delegates


class SignUpViewModel @Inject constructor(
		@ApplicationContext
		private val context: Context,
		private val webService: PocopayApi
) : ViewModel() {

	var screen by Delegates.observable(ABOUT) { _, _, _ -> handleScreenChanged() }

	var login = ""

	var password = ""

	var termsAccepted = false

	val countries = context.resources.getStringArray(R.array.countries)

	var countryIndex = 0

	var city = ""

	var zipCode = ""

	val screenTitle = MutableLiveData<String>()

	val buttonText = ObservableField("")

	val isInProgress = ObservableField(false)

	val error = SingleLiveEvent<String>()

	val onCreated = SingleLiveEvent<Unit>()

	val onSignUpComplete = SingleLiveEvent<Unit>()

	var onShowCredentialsScreen = SingleLiveEvent<Unit>()

	var onShowAddressScreen = SingleLiveEvent<Unit>()

	private val errorHandler = ApiErrorHandler(context, error) { attemptSignup() }

	init {
		val defaultCountry = Locale.getDefault().displayCountry
		countryIndex = max(countries.indexOf(defaultCountry), 0)
		onCreated()
	}

	fun onButtonClick() {
		when (screen) {
			ABOUT -> moveToCredentialsScreen()

			CREDENTIALS -> moveToAddressScreen()

			ADDRESS -> {
				errorHandler.resetTimeoutCounter()
				attemptSignup()
			}
		}
	}

	fun handleScreenChanged() {
		screenTitle.value = "${screen.ordinal + 1}/${Screen.values().size} $login"

		when (screen) {

			ABOUT -> buttonText.set(context.getString(R.string.sign_up_next))

			CREDENTIALS -> buttonText.set(context.getString(R.string.sign_up_next))

			ADDRESS -> buttonText.set(context.getString(R.string.sign_up_sign_up))
		}
	}

	private fun moveToCredentialsScreen() = onShowCredentialsScreen()

	private fun moveToAddressScreen() {
		if (login.isEmpty() || password.isEmpty()) {
			error.value = context.getString(R.string.error_empty_credentials)
			return
		}
		if (!termsAccepted) {
			error.value = context.getString(R.string.error_terms_not_accepted)
			return
		}
		onShowAddressScreen()
	}

	private fun attemptSignup() {
		if (isInProgress.get()) {
			return
		}

		isInProgress.set(true)

		val request = SignUpRequest(
				login,
				password,
				countries[countryIndex],
				city,
				zipCode
		)

		webService.post(request).enqueue(object : Callback<ApiResponse> {

			override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
				isInProgress.set(false)
				error.value = context.getString(R.string.error_unknown)
			}

			override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
				isInProgress.set(false)

				if (response.isSuccessful) {
					onSignUpComplete()
				} else {
					errorHandler.process(response)
				}
			}
		})
	}

	enum class Screen(val layoutId: Int) {
		ABOUT(R.layout.fragment_sign_up_about),
		CREDENTIALS(R.layout.fragment_sign_up_credentials),
		ADDRESS(R.layout.fragment_sign_up_address)
	}
}
