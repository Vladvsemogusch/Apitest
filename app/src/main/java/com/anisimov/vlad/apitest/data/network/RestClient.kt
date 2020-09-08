package com.anisimov.vlad.apitest.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

class RestClient {
    companion object {
        private const val BASE_URL = "https://api.github.com/"
        //  Recommended header
        private const val ACCEPT_HEADER_NAME = "Accept"
        private const val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"
        val instance: RestClient by lazy { RestClient() }
    }


    val githubApi: GithubApi by lazy {
        buildRetrofit().create(GithubApi::class.java)
    }


    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(BASE_URL)
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(HeaderInterceptor())
        .build()

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE)
                .build()
            return chain.proceed(request)
        }
    }
}


