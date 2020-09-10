package com.anisimov.vlad.apitest.domain.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anisimov.vlad.apitest.data.repository.RepoListRepository
import com.anisimov.vlad.apitest.domain.model.NewReposEvent
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import kotlin.math.ceil

class RepoListViewModel(app: Application) : BaseViewModel<RepoListRepository>(app) {
    companion object {
        // Must be even
        const val ITEMS_PER_PAGE_UI = 30
    }

    val oLoading = MutableLiveData(false)
    private val oNewReposEvent: MutableLiveData<NewReposEvent> = LiveEvent<NewReposEvent>()
    private var totalPageCount = 0
    private var totalItemCount = 0
    private var currentPage = 0
    private var lastQuery = ""

    init {
        repo.init(ITEMS_PER_PAGE_UI)
    }


    fun search(query: String) {
        if (query == lastQuery) {
            loadMore(query)
        } else {
            newSearch(query)
        }
    }

    private fun newSearch(query: String) {
        viewModelScope.launch {
            val newSearchResultUI = repo.getNewSearchResult(query)
            totalItemCount = newSearchResultUI.totalCount
            totalPageCount = ceil(totalItemCount.toDouble() / ITEMS_PER_PAGE_UI.toDouble()).toInt()
            val event = NewReposEvent(true, newSearchResultUI.repos)
            oNewReposEvent.postValue(event)
        }
    }

    private fun loadMore(query: String): Boolean {
        if (currentPage == totalPageCount) {
            return false
        }
        viewModelScope.launch {
            val moreRepos = repo.getMoreSearchResults(query, currentPage + 1, totalItemCount)
            oNewReposEvent.postValue(NewReposEvent(false, moreRepos))
        }
        return true
    }

    override fun provideRepo(): RepoListRepository = RepoListRepository()
}