package com.example.dkalita.pocopay.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.dkalita.pocopay.R
import com.example.dkalita.pocopay.common.lazyViewModel
import com.example.dkalita.pocopay.common.observe
import com.example.dkalita.pocopay.common.observeNotNull
import com.example.dkalita.pocopay.databinding.ActivityLoginBinding
import com.example.dkalita.pocopay.inject.createInjector
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

	companion object {

		fun start(context: Context) {
			val intent = Intent(context, LoginActivity::class.java)
			context.startActivity(intent)
		}
	}

	private val viewModel by lazyViewModel { createInjector().getLoginViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(
				this, R.layout.activity_login)
		binding.model = viewModel

		loginPasswordView.setOnEditorActionListener { _, id, _ ->
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
				viewModel.attemptLogin()
				return@setOnEditorActionListener true
			}
			return@setOnEditorActionListener false
		}

		viewModel.error.observeNotNull(this) { showError(it) }

		viewModel.onLoginComplete.observe(this) {
			SuccessActivity.start(this, R.string.success_login_successful)
		}
	}

	private fun showError(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show()
	}
}
