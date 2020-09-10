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
    val oNewReposEvent: MutableLiveData<NewReposEvent> = LiveEvent<NewReposEvent>()

    //  TODO Make non-null
    var totalItemCount = MutableLiveData(0)
    private var totalPageCount = 0
    private var currentPage = 0
    private var lastQuery = ""

    init {
        repo.init(ITEMS_PER_PAGE_UI)
    }


    fun newSearch(query: String) {
        lastQuery = query
        viewModelScope.launch {
            val newSearchResultUI = repo.getNewSearchResult(query)
            totalItemCount.value = newSearchResultUI.totalCount
            totalPageCount =
                ceil(totalItemCount.value!!.toDouble() / ITEMS_PER_PAGE_UI.toDouble()).toInt()
            val event = NewReposEvent(true, newSearchResultUI.repos)
            currentPage = 1
            oNewReposEvent.postValue(event)
        }
    }

    fun loadMore(): Boolean {
        if (currentPage == totalPageCount) {
            return false
        }
        viewModelScope.launch {
            val moreRepos =
                repo.getMoreSearchResults(lastQuery, currentPage + 1, totalItemCount.value!!)
            currentPage++
            oNewReposEvent.postValue(NewReposEvent(false, moreRepos))
        }
        return true
    }

    override fun provideRepo(): RepoListRepository = RepoListRepository()
}