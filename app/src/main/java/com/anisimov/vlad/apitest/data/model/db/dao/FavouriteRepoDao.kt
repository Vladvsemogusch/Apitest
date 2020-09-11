package com.anisimov.vlad.apitest.data.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.anisimov.vlad.apitest.data.model.db.FavoriteRepoDB

@Dao
interface FavoriteRepoDao {

    @Insert
    suspend fun add(repo: FavoriteRepoDB)

    @Delete
    suspend fun remove(repo: FavoriteRepoDB)

    @Query("SELECT * FROM FavoriteRepoDB")
    suspend fun getAll(): List<FavoriteRepoDB>

    @Query("SELECT id FROM FavoriteRepoDB")
    suspend fun getAllIds(): List<Long>
}