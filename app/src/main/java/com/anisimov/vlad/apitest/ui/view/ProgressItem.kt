package com.anisimov.vlad.apitest.ui.view

import android.animation.Animator
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.anisimov.vlad.apitest.R
import com.anisimov.vlad.apitest.ui.view.ProgressItem.ProgressViewHolder
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.Payload
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class ProgressItem : AbstractFlexibleItem<ProgressViewHolder>() {
    var status = StatusEnum.MORE_TO_LOAD
    override fun equals(other: Any?): Boolean {
        return other is ProgressItem
    }

    override fun getLayoutRes(): Int {
        return R.layout.progress_item
    }

    override fun createViewHolder(
        view: View?,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?
    ): ProgressViewHolder {
        return ProgressViewHolder(view!!, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ProgressViewHolder,
        position: Int, payloads: List<*>
    ) {
        holder.progressBar.visibility = View.GONE
        status = if (payloads.contains(Payload.NO_MORE_LOAD)) {
            StatusEnum.NO_MORE_LOAD
        } else {
            StatusEnum.MORE_TO_LOAD
        }
        when (status) {
            StatusEnum.NO_MORE_LOAD -> status = StatusEnum.MORE_TO_LOAD
            else -> holder.progressBar.visibility = View.VISIBLE
        }
    }

    class ProgressViewHolder(view: View, adapter: FlexibleAdapter<*>?) :
        FlexibleViewHolder(view, adapter) {
        var progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            AnimatorHelper.scaleAnimator(animators, itemView, 0f)
        }

    }

    enum class StatusEnum {
        MORE_TO_LOAD, NO_MORE_LOAD
    }
}