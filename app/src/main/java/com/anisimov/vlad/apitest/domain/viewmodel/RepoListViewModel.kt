package com.anisimov.vlad.apitest.domain.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anisimov.vlad.apitest.data.repository.RepoListRepository
import com.anisimov.vlad.apitest.domain.model.RepoUI
import com.anisimov.vlad.apitest.domain.model.event.NewReposEvent
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.ceil

class RepoListViewModel(app: Application) : BaseViewModel<RepoListRepository>(app) {
    companion object {
        // Must be even
        const val ITEMS_PER_PAGE_UI = 30
    }

    val oNewSearchLoading = MutableLiveData(false)
    val oNewReposEvent: MutableLiveData<NewReposEvent> = LiveEvent<NewReposEvent>()

    //  TODO Make non-null
    var totalItemCount = MutableLiveData(0)
    private var totalPageCount = -1
    private var currentPage = -1
    private var lastQuery = ""
    private var searchJobInProgress: Job? = null

    init {
        repo.init(ITEMS_PER_PAGE_UI)
    }

    override fun provideRepo(): RepoListRepository = RepoListRepository()

    fun newSearch(query: String) {
        if (lastQuery == query || query.isBlank()) {
            return
        }
        //  Cancel if something is in progress
        searchJobInProgress?.cancel()
        oNewSearchLoading.value = true
        lastQuery = query
        val job = viewModelScope.launch {
            val newSearchResultUI = repo.getNewSearchResult(query)
            totalItemCount.value = newSearchResultUI.totalCount
            totalPageCount =
                ceil(totalItemCount.value!!.toDouble() / ITEMS_PER_PAGE_UI.toDouble()).toInt()
            val event = NewReposEvent(true, newSearchResultUI.repos)
            currentPage = 1
            oNewSearchLoading.postValue(false)
            oNewReposEvent.postValue(event)
        }
        searchJobInProgress = job
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                oNewSearchLoading.value = false
                oNewReposEvent.postValue(NewReposEvent(true, ArrayList()))
            }
        }
    }

    fun loadMore() {
        if (currentPage == totalPageCount) {
            oNewReposEvent.postValue(NewReposEvent(false, null))
            return
        }
        val job = viewModelScope.launch {
            val moreRepos =
                repo.getMoreSearchResults(lastQuery, currentPage + 1, totalItemCount.value!!)
            currentPage++
            oNewReposEvent.postValue(NewReposEvent(false, moreRepos))
        }
        searchJobInProgress = job
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                oNewReposEvent.postValue(NewReposEvent(false, null))
            }
        }
    }


    fun addFavorite(repoUI: RepoUI) {
        viewModelScope.launch {
            repo.addFavorite(repoUI.id, repoUI.name, repoUI.description)
        }
    }

    fun removeFavorite(repoUI: RepoUI) {
        viewModelScope.launch {
            repo.removeFavorite(repoUI.id, repoUI.name, repoUI.description)
        }
    }

    fun forgetLastQuery() {
        lastQuery = ""
    }
}