package com.revolut.recyclerkit.delegates

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/*
 * Copyright (C) 2019 Revolut
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

open class DiffAdapter(
    delegatesManager: DelegatesManager = DelegatesManager(),
    autoScrollToTop: Boolean = false
) : AbsRecyclerDelegatesAdapter(delegatesManager) {

    private val differ: AsyncListDiffer<ListItem> = AsyncListDiffer(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(ListDiffCallback<ListItem>()).build()
    )
    private var recyclerView = WeakReference<RecyclerView>(null)
    private val commitCallback: Runnable? =
        if (autoScrollToTop) {
            Runnable {
                val rv = this.recyclerView.get() ?: error("Recycler View not attached")
                val firstVisiblePosition = (rv.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: 0
                if (firstVisiblePosition == 0) {
                    rv.scrollToPosition(0)
                }
            }
        } else {
            null
        }

    val items: List<ListItem>
        get() = differ.currentList

    override fun getItem(position: Int): ListItem = differ.currentList[position]

    fun setItems(items: List<ListItem>) = differ.submitList(items, commitCallback)

    override fun getItemCount() = differ.currentList.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = WeakReference(recyclerView)
    }

    private class ListDiffCallback<T> : DiffUtil.ItemCallback<ListItem>() {

        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.listId == newItem.listId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: ListItem, newItem: ListItem): Any? {
            return newItem.calculatePayload(oldItem)
        }
    }

}
