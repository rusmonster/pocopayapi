package com.example.dkalita.pocopay

import android.app.Application
import com.example.dkalita.pocopay.inject.ApplicationModule
import com.example.dkalita.pocopay.inject.DaggerApplicationComponent

class PocopayApplication : Application() {

	val component by lazy {
		DaggerApplicationComponent.builder()
				.applicationModule(ApplicationModule(this))
				.build()
	}
}
