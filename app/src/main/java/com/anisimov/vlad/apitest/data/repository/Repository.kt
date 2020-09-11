package com.anisimov.vlad.apitest.data.repository

import kotlinx.coroutines.cancel
import kotlinx.coroutines.yield
import retrofit2.Response
import kotlin.coroutines.coroutineContext

abstract class Repository {


    protected suspend fun <T> handleResponse(response: Response<T>): T {
        if (!response.isSuccessful) {
            coroutineContext.cancel()
        }
        yield()
        return response.body()!!
    }
}