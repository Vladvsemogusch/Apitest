package com.anisimov.vlad.apitest.ui.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.anisimov.vlad.apitest.R
import com.anisimov.vlad.apitest.domain.model.RepoUI
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

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
        adapter: FlexibleAdapter<IFlexible<*>>
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

    //  ImageView not available from inside
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
