package com.revolut.recyclerkit.sample.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.revolut.recyclerkit.delegates.BaseRecyclerViewDelegate
import com.revolut.recyclerkit.delegates.BaseRecyclerViewHolder
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.delegates.HasNestedDelegates
import com.revolut.recyclerkit.sample.delegates.PreconditionsCheckNestedItemsDelegate.Model
import com.revolut.recyclerkit.sample.delegates.PreconditionsCheckNestedItemsDelegate.ViewHolder

class PreconditionsCheckNestedItemsDelegate(
    override val delegates: List<BaseRecyclerViewDelegate<*, *>>
) : BaseRecyclerViewDelegate<Model, ViewHolder>(
    viewType = 0,
    rule = { _, data -> data is Model }
), HasNestedDelegates {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))

    data class Model(override val listId: String) : ListItem

    class ViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView)
}