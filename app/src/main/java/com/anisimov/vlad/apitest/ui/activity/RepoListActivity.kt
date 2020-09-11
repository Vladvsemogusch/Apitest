package com.anisimov.vlad.apitest.ui.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anisimov.vlad.apitest.R
import com.anisimov.vlad.apitest.domain.model.RepoUI
import com.anisimov.vlad.apitest.domain.viewmodel.RepoListViewModel
import com.anisimov.vlad.apitest.ui.view.ProgressItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class RepoListActivity : BaseActivity<RepoListViewModel>() {
    private lateinit var listAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>
    private lateinit var progressItem: ProgressItem
    override fun provideViewModelClass(): Class<RepoListViewModel> = RepoListViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //  Make room for SearchView
        title = ""
        setupRepoList()
        setupLoading()
        setupSearch()
    }

    private fun setupSearch() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
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
        val layoutManager = LinearLayoutManager(this)
        rvRepoList.layoutManager = layoutManager
        listAdapter = FlexibleAdapter<AbstractFlexibleItem<*>>(ArrayList())
        rvRepoList.addItemDecoration(
            DividerItemDecoration(
                rvRepoList.context,
                layoutManager.orientation
            )
        )
        rvRepoList.adapter = listAdapter
        viewModel.oNewReposEvent.observe(this) { newReposEvent ->
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
        progressItem = ProgressItem()
        listAdapter.setEndlessScrollListener(SimpleEndlessScrollListener(), progressItem)
        viewModel.totalItemCount.observe(this) { listAdapter.setEndlessTargetCount(it) }
        // Favorites
        listAdapter.addListener(FlexibleAdapter.OnItemClickListener { view: View, position: Int ->
            if (view.id == R.id.ivFavorite) {
                val item = listAdapter.getItem(position) as RepoAdapterItem
                if (item.isFavorite()) {
                    viewModel.removeFavorite(item.repo)
                } else {
                    viewModel.addFavorite(item.repo)
                }
                //  ImageView not available from inside
                item.toggleFavorite(view as ImageView)
            }
            true
        })
    }

    private fun setupLoading() {
        viewModel.oNewSearchLoading.observe(this) { loading ->
            if (loading) {
                loadingOverlay.visibility = VISIBLE
            } else {
                loadingOverlay.visibility = GONE
            }
        }
    }

    class RepoAdapterItem(val repo: RepoUI) :
        AbstractFlexibleItem<RepoAdapterItem.RepoViewHolder>() {


        override fun equals(other: Any?): Boolean {
            if (other is RepoAdapterItem) {
                return other.repo.id == repo.id
            }
            return false
        }

        override fun hashCode(): Int {
            return repo.id.hashCode()
        }

        override fun getLayoutRes(): Int {
            return R.layout.repo_list_item
        }

        override fun createViewHolder(
            view: View,
            adapter: FlexibleAdapter<IFlexible<*>?>?
        ): RepoViewHolder {
            return RepoViewHolder(view, adapter)
        }

        override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<*>?>?, holder: RepoViewHolder,
            position: Int,
            payloads: List<Any>
        ) {
            holder.tvRepoName.text = repo.name
            holder.tvRepoDescription.text = repo.description
            @DrawableRes val imgResId = getFavoriteImageResId()
            holder.ivIsFavorite.setImageResource(imgResId)
        }

        private fun getFavoriteImageResId(isFavorite: Boolean = repo.isFavorite) = if (isFavorite) {
            R.drawable.ic_heart_full
        } else {
            R.drawable.ic_heart_empty
        }

        fun isFavorite(): Boolean = repo.isFavorite

        fun toggleFavorite(ivFavorite: ImageView) {
            val isFavorite = !repo.isFavorite
            repo.isFavorite = isFavorite
            val imgResId = getFavoriteImageResId(isFavorite)
            ivFavorite.setImageResource(imgResId)
        }


        class RepoViewHolder(view: View, adapter: FlexibleAdapter<*>?) :
            FlexibleViewHolder(view, adapter) {
            val tvRepoName: TextView = view.findViewById(R.id.tvRepoName)
            val tvRepoDescription: TextView = view.findViewById(R.id.tvRepoDescription)
            val ivIsFavorite: ImageView = view.findViewById(R.id.ivFavorite)

            init {
                ivIsFavorite.setOnClickListener(this)
            }
        }
    }

    inner class SimpleEndlessScrollListener : FlexibleAdapter.EndlessScrollListener {
        override fun noMoreLoad(newItemsSize: Int) {

        }

        override fun onLoadMore(lastPosition: Int, currentPage: Int) {
            viewModel.loadMore()
        }

    }
}