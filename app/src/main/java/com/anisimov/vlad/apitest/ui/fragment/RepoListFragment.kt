package com.anisimov.vlad.apitest.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anisimov.vlad.apitest.R
import com.anisimov.vlad.apitest.domain.viewmodel.RepoListViewModel
import com.anisimov.vlad.apitest.ui.item.RepoAdapterItem
import com.anisimov.vlad.apitest.ui.view.ProgressItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.fragment_repo_list.*
import kotlinx.android.synthetic.main.search_toolbar.*


class RepoListFragment : BaseFragment<RepoListViewModel>() {
    private lateinit var listAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>
    override fun provideViewModelClass(): Class<RepoListViewModel> = RepoListViewModel::class.java
    override fun provideLayoutRes(): Int = R.layout.fragment_repo_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar("",false)
        setupRepoList()
        setupLoading()
        setupSearch()
    }



    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                handleNewSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //   Could add live search with debounce
                return false
            }
        })

    }

    private fun handleNewSearch(query: String) {
        viewModel.newSearch(query)
    }

    @SuppressLint("Range")
    private fun setupRepoList() {
        val layoutManager = LinearLayoutManager(requireContext())
        rvRepoList.layoutManager = layoutManager
        listAdapter = FlexibleAdapter<AbstractFlexibleItem<*>>(ArrayList())
        rvRepoList.addItemDecoration(
                DividerItemDecoration(
                        rvRepoList.context,
                        layoutManager.orientation
                )
        )
        rvRepoList.adapter = listAdapter
        viewModel.oNewReposEvent.observe(viewLifecycleOwner) { newReposEvent ->
            if (!newReposEvent.newSearch && newReposEvent.repos == null) {
                listAdapter.onLoadMoreComplete(null)
                return@observe
            }
            val adapterItems = newReposEvent.repos!!.map { RepoAdapterItem(it) }
            if (newReposEvent.newSearch) {
                listAdapter.clear()
                listAdapter.addItems(-1, adapterItems)
            } else {
                listAdapter.onLoadMoreComplete(adapterItems)
            }
        }
        //  Setup endless scroll
        val progressItem = ProgressItem()
        listAdapter.setEndlessScrollListener(SimpleEndlessScrollListener(), progressItem)
        viewModel.totalItemCount.observe(viewLifecycleOwner) { listAdapter.setEndlessTargetCount(it) }
        // Favorites
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

    private fun setupLoading() {
        viewModel.oNewSearchLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadingOverlay.visibility = VISIBLE
            } else {
                loadingOverlay.visibility = GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_favorite -> {
            nav.navigate(R.id.action_repoListFragment_to_favoritesFragment)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetQuery()
    }


    inner class SimpleEndlessScrollListener : FlexibleAdapter.EndlessScrollListener {
        override fun noMoreLoad(newItemsSize: Int) {

        }

        override fun onLoadMore(lastPosition: Int, currentPage: Int) {
            viewModel.loadMore()
        }

    }


}