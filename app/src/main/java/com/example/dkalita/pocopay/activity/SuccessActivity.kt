package com.example.dkalita.pocopay.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.dkalita.pocopay.R
import kotlinx.android.synthetic.main.activity_success.*

class SuccessActivity : AppCompatActivity() {

	companion object {

		private val EXTRA_TEXT_ID = "EXTRA_TEXT_ID"

		fun start(context: Context, textId: Int) {
			val intent = Intent(context, SuccessActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
			intent.putExtra(EXTRA_TEXT_ID, textId)

			context.startActivity(intent)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_success)

		val textId = intent.getIntExtra(EXTRA_TEXT_ID, 0)
		successTextView.setText(textId)

		successButtonView.setOnClickListener { finish() }
	}
}
