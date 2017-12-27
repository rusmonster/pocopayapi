package com.example.dkalita.pocopay

import android.arch.core.executor.TaskExecutor
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiError
import com.example.dkalita.pocopay.datasource.PocopayApi.ApiResponse
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.powermock.api.mockito.PowerMockito
import retrofit2.Response

fun <T : Any?> whenCall(methodCall: T) = PowerMockito.`when`(methodCall)

class ImmediateExecutor : TaskExecutor() {

	override fun isMainThread() = true

	override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

	override fun postToMainThread(runnable: Runnable) = runnable.run()
}

fun createErrorResponse(code: Int, error: String): Response<ApiResponse> {
	val mediaType = MediaType.parse("application/json; charset=utf-8")
	val json = Gson().toJson(ApiError(error))
	val body = ResponseBody.create(mediaType, json)
	return Response.error<ApiResponse>(code, body)
}
