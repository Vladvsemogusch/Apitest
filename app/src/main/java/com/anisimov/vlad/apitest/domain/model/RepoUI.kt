package com.anisimov.vlad.apitest.domain.model

data class RepoUI(
    val id: Long,
    val name: String,
    val description: String,
    var isFavorite: Boolean
) {
}