package com.anisimov.vlad.apitest.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anisimov.vlad.apitest.data.model.db.FavoriteRepoDB
import com.anisimov.vlad.apitest.data.model.db.dao.FavoriteRepoDao
import com.anisimov.vlad.apitest.ui.BaseApp

@Database(entities = [FavoriteRepoDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DB_FILE_NAME = "favoriteRepositoriesDatabase"
        val instance: AppDatabase by lazy {
            Room.databaseBuilder(
                BaseApp.getAppContext(),
                AppDatabase::class.java,
                DB_FILE_NAME
            )
                .build()
        }
    }

    abstract fun getFavoriteRepoDao(): FavoriteRepoDao

}