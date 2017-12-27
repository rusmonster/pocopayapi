package com.example.dkalita.pocopay.inject

import com.example.dkalita.pocopay.viewmodel.LoginViewModel
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

	fun loginActivityComponent(): LoginActivityComponent

	fun signUpActivityComponent(): SignUpActivityComponent
}

@Subcomponent
interface LoginActivityComponent {

	fun getLoginViewModel(): LoginViewModel
}

@Subcomponent
interface SignUpActivityComponent {

	fun getSignUpViewModel(): SignUpViewModel
}
