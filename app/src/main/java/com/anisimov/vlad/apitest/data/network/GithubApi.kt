package com.anisimov.vlad.apitest.data.network

import com.anisimov.vlad.apitest.data.model.network.SearchResponseNetwork
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : SearchResponseNetwork
}