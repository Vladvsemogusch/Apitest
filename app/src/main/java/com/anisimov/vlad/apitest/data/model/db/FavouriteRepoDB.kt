package com.anisimov.vlad.apitest.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteRepoDB(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String
)