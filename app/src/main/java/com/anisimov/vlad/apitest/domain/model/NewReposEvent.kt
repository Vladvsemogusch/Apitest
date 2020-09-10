package com.anisimov.vlad.apitest.domain.model

data class NewReposEvent(val newSearch: Boolean, val repos: List<RepoUI>)