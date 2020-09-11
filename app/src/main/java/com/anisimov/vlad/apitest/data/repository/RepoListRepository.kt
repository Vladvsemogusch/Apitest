package com.anisimov.vlad.apitest.data.repository

import com.anisimov.vlad.apitest.data.db.AppDatabase
import com.anisimov.vlad.apitest.data.model.db.FavoriteRepoDB
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
import retrofit2.Response

class RepoListRepository : Repository() {
    companion object {
        const val DEFAULT_ITEMS_PER_PAGE = 15
    }

    private val githubApi = RestClient.instance.githubApi
    private val favoriteRepoDao = AppDatabase.instance.getFavoriteRepoDao()
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
        val searchResponseFirstPage =
            handleResponse(githubApi.getRepositories(query, 0, itemsPerPageNetwork))
        tempReposNetwork += searchResponseFirstPage.items
        if (searchResponseFirstPage.totalCount > itemsPerPageNetwork) {
            val searchResponseSecondPage =
                handleResponse(githubApi.getRepositories(query, 1, itemsPerPageNetwork))
            tempReposNetwork += searchResponseSecondPage.items
        }
        val favoriteRepoIds = getFavoriteRepoIds()
        val reposUI = tempReposNetwork.map {
            val isFavorite = favoriteRepoIds.contains(it.id)
            RepoUI(it.id, it.name, it.description ?: "", isFavorite)
        }
        NewSearchResultUI(searchResponseFirstPage.totalCount, reposUI)
    }

    private suspend fun getFavoriteRepoIds(): List<Long> = favoriteRepoDao.getAllIds()

    // Needs more parameters to know if we have enough items to load
    suspend fun getMoreSearchResults(
        query: String,
        pageUI: Int,
        totalCount: Int
    ) = withContext(Dispatchers.IO) {
        val tempReposNetwork = ArrayList<RepoItemNetwork>()
        val startItemCount = (pageUI - 1) * itemsPerPageUI
        var searchResponseFirstPage: Deferred<Response<SearchResponseNetwork>>? = null
        // Check if we actually have something to load
        if (startItemCount + 1 <= totalCount) {
            val pageNetwork = pageUI * 2 - 1
            searchResponseFirstPage =
                async { githubApi.getRepositories(query, pageNetwork, itemsPerPageNetwork) }

        }
        var searchResponseSecondPage: Deferred<Response<SearchResponseNetwork>>? = null
        //  Check if we have enough items to load second page
        if (startItemCount + itemsPerPageNetwork + 1 <= totalCount) {
            val pageNetwork = pageUI * 2
            searchResponseSecondPage =
                async { githubApi.getRepositories(query, pageNetwork, itemsPerPageNetwork) }
        }
        searchResponseFirstPage?.let { tempReposNetwork += handleResponse(it.await()).items }
        searchResponseSecondPage?.let { tempReposNetwork += handleResponse(it.await()).items }
        val favoriteRepoIds = getFavoriteRepoIds()
        tempReposNetwork.map {
            val isFavorite = favoriteRepoIds.contains(it.id)
            RepoUI(it.id, it.name, it.description ?: "", isFavorite)
        }
    }

    suspend fun addFavorite(id: Long, name: String, description: String) =
        withContext(Dispatchers.IO) {
            val favoriteRepoDB = FavoriteRepoDB(id, name, description)
            favoriteRepoDao.add(favoriteRepoDB)
        }

    suspend fun removeFavorite(id: Long, name: String, description: String) =
        withContext(Dispatchers.IO) {
            val favoriteRepoDB = FavoriteRepoDB(id, name, description)
            favoriteRepoDao.remove(favoriteRepoDB)
        }


}
