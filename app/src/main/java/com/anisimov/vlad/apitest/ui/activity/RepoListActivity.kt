package com.anisimov.vlad.apitest.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
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


class RepoListActivity : BaseActivity<RepoListViewModel>() {
    lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>
    lateinit var progressItem: ProgressItem
    override fun provideViewModelClass(): Class<RepoListViewModel> = RepoListViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRepoList()
    }

    @SuppressLint("Range")
    private fun setupRepoList() {
        val layoutManager = LinearLayoutManager(this)
        rvRepoList.layoutManager = layoutManager
        adapter = FlexibleAdapter<AbstractFlexibleItem<*>>(ArrayList())
        rvRepoList.addItemDecoration(
            DividerItemDecoration(
                rvRepoList.context,
                layoutManager.orientation
            )
        )
        rvRepoList.adapter = adapter
        viewModel.oNewReposEvent.observe(this) { newReposEvent ->
            val adapterItems = newReposEvent.repos.map { RepoAdapterItem(it) }
            if (newReposEvent.newSearch) {
                adapter.clear()
                adapter.addItems(-1, adapterItems)
            } else {
                adapter.onLoadMoreComplete(adapterItems)
            }

        }
        viewModel.newSearch("tetris")
        //  Set endless scroll
        progressItem = ProgressItem()
        adapter.setEndlessScrollListener(SimpleEndlessScrollListener(), progressItem)
        viewModel.totalItemCount.observe(this) { adapter.setEndlessTargetCount(it) }

    }

    class RepoAdapterItem(private val repo: RepoUI) :
        AbstractFlexibleItem<RepoAdapterItem.RepoViewHolder>() {

        override fun equals(other: Any?): Boolean {
            if (other is RepoAdapterItem) {
                return other.repo.id == repo.id
            }
            return false
        }

        override fun hashCode(): Int {
            return repo.id
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
        }

        class RepoViewHolder(view: View, adapter: FlexibleAdapter<*>?) :
            FlexibleViewHolder(view, adapter) {
            val tvRepoName: TextView = view.findViewById(R.id.tvRepoName)
            val tvRepoDescription: TextView = view.findViewById(R.id.tvRepoDescription)

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