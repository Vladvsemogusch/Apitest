package com.anisimov.vlad.apitest.domain.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anisimov.vlad.apitest.data.repository.FavoritesRepository
import com.anisimov.vlad.apitest.domain.model.RepoUI
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : BaseViewModel<FavoritesRepository>(app) {
    val oFavoriteRepos = MutableLiveData<List<RepoUI>>(ArrayList())
    override fun provideRepo(): FavoritesRepository = FavoritesRepository()

    init {
        viewModelScope.launch {
            oFavoriteRepos.value = repo.getAllFavoriteRepos()
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
}