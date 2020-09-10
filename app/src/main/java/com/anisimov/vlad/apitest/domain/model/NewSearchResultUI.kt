package com.anisimov.vlad.apitest.domain.model

data class NewSearchResultUI(
    val totalCount: Int,
    val repos: List<RepoUI>
)