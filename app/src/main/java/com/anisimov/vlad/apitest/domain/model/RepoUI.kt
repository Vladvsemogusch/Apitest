package com.anisimov.vlad.apitest.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class RepoUI(
        val id: Long,
        val name: String,
        val description: String,
        var isFavorite: Boolean
)