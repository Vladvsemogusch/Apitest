package com.anisimov.vlad.apitest.data.repository

import com.anisimov.vlad.apitest.data.model.network.RepoItemNetwork
import com.anisimov.vlad.apitest.data.model.network.SearchResponseNetwork
import com.anisimov.vlad.apitest.data.network.RestClient
import com.anisimov.vlad.apitest.domain.model.NewSearchResultUI
import com.anisimov.vlad.apitest.domain.model.RepoUI
import com.anisimov.vlad.apitest.domain.viewmodel.RepoListViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class RepoListRepository : Repository {
    companion object {
        const val DEFAULT_ITEMS_PER_PAGE = 15
    }

    private val githubApi = RestClient.instance.githubApi
    private var itemsPerPageNetwork = DEFAULT_ITEMS_PER_PAGE
    private var itemsPerPageUI = DEFAULT_ITEMS_PER_PAGE * 2

    fun init(itemsPerPageUI: Int) {
        if (itemsPerPageUI % 2 != 0) {
            throw IllegalArgumentException("Per page not even")
        }
        this.itemsPerPageUI = itemsPerPageUI
        itemsPerPageNetwork = RepoListViewModel.ITEMS_PER_PAGE_UI / 2
    }


    suspend fun getNewSearchResult(
        query: String
    ) = withContext(Dispatchers.IO) {
        val tempReposNetwork = ArrayList<RepoItemNetwork>()
        //  Can't launch in parallel until totalCount is known
        val searchResponseFirstPage = githubApi.getRepositories(query, 0, itemsPerPageNetwork)
        tempReposNetwork += searchResponseFirstPage.items
        if (searchResponseFirstPage.totalCount > itemsPerPageNetwork) {
            val searchResponseSecondPage = githubApi.getRepositories(query, 1, itemsPerPageNetwork)
            tempReposNetwork += searchResponseSecondPage.items
        }
        val reposUI = tempReposNetwork.map { RepoUI(it.id, it.name, it.description) }
        NewSearchResultUI(searchResponseFirstPage.totalCount, reposUI)
    }

    // Needs more parameters to know if we have enough items to load
    suspend fun getMoreSearchResults(
        query: String,
        pageUI: Int,
        totalCount: Int
    ) = withContext(Dispatchers.IO) {
        val tempReposNetwork = ArrayList<RepoItemNetwork>()
        val startItemCount = (pageUI - 1) * itemsPerPageUI
        var searchResponseFirstPage: Deferred<SearchResponseNetwork>? = null
        // Check if we actually have something to load
        if (startItemCount + 1 <= totalCount) {
            val pageNetwork = pageUI * 2 - 1
            searchResponseFirstPage =
                async { githubApi.getRepositories(query, pageNetwork, itemsPerPageNetwork) }

        }
        var searchResponseSecondPage: Deferred<SearchResponseNetwork>? = null
        //  Check if we have enough items to load second page
        if (startItemCount + itemsPerPageNetwork + 1 <= totalCount) {
            val pageNetwork = pageUI * 2
            searchResponseSecondPage =
                async{githubApi.getRepositories(query, pageNetwork, itemsPerPageNetwork)}

        }
        searchResponseFirstPage?.let {  tempReposNetwork += it.await().items }
        searchResponseSecondPage?.let {  tempReposNetwork += it.await().items }
        tempReposNetwork.map { RepoUI(it.id, it.name, it.description) }
    }


}
