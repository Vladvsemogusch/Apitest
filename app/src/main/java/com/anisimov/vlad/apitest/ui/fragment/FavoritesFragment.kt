package com.anisimov.vlad.apitest.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anisimov.vlad.apitest.R
import com.anisimov.vlad.apitest.domain.viewmodel.FavoritesViewModel
import com.anisimov.vlad.apitest.ui.item.RepoAdapterItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : BaseFragment<FavoritesViewModel>() {
    private lateinit var listAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>
    override fun provideViewModelClass(): Class<FavoritesViewModel> = FavoritesViewModel::class.java
    override fun provideLayoutRes(): Int = R.layout.fragment_favorites

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(getString(R.string.favorites))
        setupRepoList()
    }

    @SuppressLint("Range")
    private fun setupRepoList() {
        val layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.layoutManager = layoutManager
        listAdapter = FlexibleAdapter<AbstractFlexibleItem<*>>(ArrayList())
        rvFavorites.addItemDecoration(
                DividerItemDecoration(
                        rvFavorites.context,
                        layoutManager.orientation
                )
        )
        rvFavorites.adapter = listAdapter
        viewModel.oFavoriteRepos.observe(viewLifecycleOwner) { favoriteRepos ->
            val adapterItems = favoriteRepos.map { RepoAdapterItem(it) }
                listAdapter.addItems(-1, adapterItems)
        }
        listAdapter.addListener(FlexibleAdapter.OnItemClickListener { view: View, position: Int ->
            if (view.id == R.id.ivFavorite) {
                val item = listAdapter.getItem(position) as RepoAdapterItem
                if (item.isFavorite()) {
                    viewModel.removeFavorite(item.repo)
                } else {
                    viewModel.addFavorite(item.repo)
                }
                item.toggleFavorite(view as ImageView)
            }
            true
        })
    }
}