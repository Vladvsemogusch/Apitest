package com.anisimov.vlad.apitest.data.model.network


import com.google.gson.annotations.SerializedName

data class SearchResponseNetwork(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<RepoItemNetwork>,
    @SerializedName("total_count")
    val totalCount: Int
)