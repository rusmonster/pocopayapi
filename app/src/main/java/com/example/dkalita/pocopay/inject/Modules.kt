package com.example.dkalita.pocopay.inject

import android.app.Application
import android.content.Context
import com.example.dkalita.pocopay.datasource.PocopayApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(val application: Application) {

	@Provides
	@Singleton
	@ApplicationContext
	fun provideContext(): Context = application

	@Provides
	@Singleton
	fun providePocopayApi(): PocopayApi = PocopayApi.create()
}
