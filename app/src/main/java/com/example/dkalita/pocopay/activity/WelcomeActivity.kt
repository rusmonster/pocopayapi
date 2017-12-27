package com.example.dkalita.pocopay.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.dkalita.pocopay.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		window.setBackgroundDrawableResource(R.color.colorPrimary)
		setContentView(R.layout.activity_welcome)

		loginButtonView.setOnClickListener { LoginActivity.start(this) }
		signUpButtonView.setOnClickListener { SignUpActivity.start(this) }
	}
}
