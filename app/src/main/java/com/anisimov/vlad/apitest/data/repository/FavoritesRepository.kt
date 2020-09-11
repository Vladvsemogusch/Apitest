package com.anisimov.vlad.apitest.data.repository

import com.anisimov.vlad.apitest.data.db.AppDatabase
import com.anisimov.vlad.apitest.data.model.db.FavoriteRepoDB
import com.anisimov.vlad.apitest.domain.model.RepoUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesRepository : Repository() {
    private val favoriteRepoDao = AppDatabase.instance.getFavoriteRepoDao()

    suspend fun getAllFavoriteRepos(): List<RepoUI> {
        val favoriteRepos = favoriteRepoDao.getAll()
        return favoriteRepos.map { RepoUI(it.id,it.name,it.description,true) }
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