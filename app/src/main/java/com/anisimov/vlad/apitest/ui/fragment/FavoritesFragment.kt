package com.anisimov.vlad.apitest.ui.fragment

import com.anisimov.vlad.apitest.R
import com.anisimov.vlad.apitest.domain.viewmodel.FavoritesViewModel

class FavoritesFragment() : BaseFragment<FavoritesViewModel>() {
    override fun provideViewModelClass(): Class<FavoritesViewModel> = FavoritesViewModel::class.java
    override fun provideLayoutRes(): Int = R.layout.fragment_favorites


}