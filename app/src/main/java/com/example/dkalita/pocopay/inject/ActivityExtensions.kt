package com.example.dkalita.pocopay.inject

import android.app.Activity
import com.example.dkalita.pocopay.PocopayApplication
import com.example.dkalita.pocopay.activity.LoginActivity
import com.example.dkalita.pocopay.activity.SignUpActivity

private val Activity.applicationComponent get() = (application as PocopayApplication).component

fun LoginActivity.createInjector() = applicationComponent.loginActivityComponent()

fun SignUpActivity.createInjector() = applicationComponent.signUpActivityComponent()
