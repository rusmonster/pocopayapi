package com.example.dkalita.pocopay.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider.Factory
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity


inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline block: (T?) -> Unit) {
	val observer = Observer<T> { block(it) }
	observe(owner, observer)
}

inline fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, crossinline block: (T) -> Unit) {
	observe(owner) { if (it != null) block(it) }
}

inline fun <reified T : ViewModel> FragmentActivity.lazyViewModel(
		crossinline create: () -> T) = lazy(LazyThreadSafetyMode.NONE) {

	val factory = object : Factory {
		@Suppress("UNCHECKED_CAST")
		override fun <R : ViewModel> create(modelClass: Class<R>): R = create() as R
	}
	return@lazy ViewModelProviders.of(this, factory).get(T::class.java)
}
