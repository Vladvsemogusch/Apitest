package com.anisimov.vlad.apitest.domain.model.event

import com.anisimov.vlad.apitest.domain.model.RepoUI

data class NewReposEvent(val newSearch: Boolean, val repos: List<RepoUI>?)