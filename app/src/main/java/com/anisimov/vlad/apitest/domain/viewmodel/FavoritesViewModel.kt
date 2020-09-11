package com.anisimov.vlad.apitest.domain.viewmodel

import android.app.Application
import com.anisimov.vlad.apitest.data.repository.FavoritesRepository

class FavoritesViewModel(app: Application) : BaseViewModel<FavoritesRepository>(app) {
    override fun provideRepo(): FavoritesRepository = FavoritesRepository()
}