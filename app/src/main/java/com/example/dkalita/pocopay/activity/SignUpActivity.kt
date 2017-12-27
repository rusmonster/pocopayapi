package com.example.dkalita.pocopay.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dkalita.pocopay.BR
import com.example.dkalita.pocopay.R
import com.example.dkalita.pocopay.common.lazyViewModel
import com.example.dkalita.pocopay.common.observe
import com.example.dkalita.pocopay.common.observeNotNull
import com.example.dkalita.pocopay.databinding.ActivitySignUpBinding
import com.example.dkalita.pocopay.inject.createInjector
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen.ABOUT
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen.ADDRESS
import com.example.dkalita.pocopay.viewmodel.SignUpViewModel.Screen.CREDENTIALS

class SignUpActivity : AppCompatActivity() {

	companion object {

		fun start(context: Context) {
			val intent = Intent(context, SignUpActivity::class.java)
			context.startActivity(intent)
		}
	}

	private val viewModel by lazyViewModel { createInjector().getSignUpViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val binding = DataBindingUtil.setContentView<ActivitySignUpBinding>(
				this, R.layout.activity_sign_up)
		binding.model = viewModel

		viewModel.screenTitle.observeNotNull(this) { title = it }
		viewModel.error.observeNotNull(this) { showError(it) }

		viewModel.onCreated.observe(this) {
			initFragment()
		}

		viewModel.onShowCredentialsScreen.observe(this) {
			replaceFragment(SignUpCredentialsFragment())
		}

		viewModel.onShowAddressScreen.observe(this) {
			replaceFragment(SignUpAddressFragment())
		}

		viewModel.onSignUpComplete.observe(this) {
			SuccessActivity.start(this, R.string.success_sign_up_successful)
		}
	}

	private fun initFragment() {
		supportFragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE);
		supportFragmentManager.beginTransaction()
				.add(R.id.signUpContainer, SignUpAboutFragment())
				.commitAllowingStateLoss()
	}

	private fun replaceFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
				.replace(R.id.signUpContainer, fragment)
				.addToBackStack(null)
				.commitAllowingStateLoss()
	}

	private fun showError(message: String) {
		AlertDialog.Builder(this)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
				.show()
	}
}

abstract class BaseSignUpFragment(private val screen: Screen) : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)

		val viewModel = ViewModelProviders.of(activity!!).get(SignUpViewModel::class.java)
		viewModel.screen = screen

		val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, screen.layoutId, container, false)
		binding.setVariable(BR.model, viewModel)
		return binding.root
	}
}

class SignUpAboutFragment : BaseSignUpFragment(ABOUT)

class SignUpCredentialsFragment : BaseSignUpFragment(CREDENTIALS)

class SignUpAddressFragment : BaseSignUpFragment(ADDRESS)
