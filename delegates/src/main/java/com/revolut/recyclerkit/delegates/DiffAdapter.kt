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
    async: Boolean = true,
    autoScrollToTop: Boolean = false
) : AbsRecyclerDelegatesAdapter(delegatesManager) {

    private interface DifferDelegate {
        val items: List<ListItem>
        fun setItems(items: List<ListItem>, recyclerView: RecyclerView)
    }

    private val differDelegate = if (async) AsyncDifferDelegate(this, autoScrollToTop) else SyncDifferDelegate(this, autoScrollToTop)
    protected var recyclerView = WeakReference<RecyclerView>(null)
        private set

    open val items: List<ListItem>
        get() = differDelegate.items

    override fun getItem(position: Int): ListItem = differDelegate.items[position]

    open fun setItems(items: List<ListItem>) {
        val rv = recyclerView.get() ?: error("Recycler View not attached")
        differDelegate.setItems(items, rv)
    }

    override fun getItemCount() = differDelegate.items.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = WeakReference(recyclerView)
    }

    private class AsyncDifferDelegate(
        adapter: RecyclerView.Adapter<*>,
        private val autoScrollToTop: Boolean
    ) : DifferDelegate {
        private val differ: AsyncListDiffer<ListItem> = AsyncListDiffer(
            AdapterListUpdateCallback(adapter),
            AsyncDifferConfig.Builder(ListDiffItemCallback<ListItem>()).build()
        )
        override val items: List<ListItem>
            get() = differ.currentList

        override fun setItems(items: List<ListItem>, recyclerView: RecyclerView) {
            val layoutManager = recyclerView.layoutManager

            val firstVisiblePosition = if (autoScrollToTop && layoutManager is LinearLayoutManager) {
                layoutManager.findFirstCompletelyVisibleItemPosition()
            } else {
                -1
            }

            differ.submitList(items) {
                if (firstVisiblePosition == 0) {
                    recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    private class SyncDifferDelegate(
        private val adapter: RecyclerView.Adapter<*>,
        private val autoScrollToTop: Boolean
    ) : DifferDelegate {
        override val items = ArrayList<ListItem>()

        override fun setItems(items: List<ListItem>, recyclerView: RecyclerView) {
            val (diffResult, newList) = calculateDiff(items)
            dispatchDiffInternal(diffResult, newList, recyclerView)
        }

        private fun dispatchDiffInternal(diffResult: DiffUtil.DiffResult, newList: List<ListItem>, recyclerView: RecyclerView) {
            val firstVisiblePosition = if (autoScrollToTop) {
                when (val lm = recyclerView.layoutManager) {
                    is LinearLayoutManager -> lm.findFirstCompletelyVisibleItemPosition()
                    else -> 0
                }
            } else {
                -1
            }

            val dispatchDiff: () -> Unit = {
                items.clear()
                items.addAll(newList)

                diffResult.dispatchUpdatesTo(adapter)
            }

            if (recyclerView.isComputingLayout) {
                recyclerView.post { dispatchDiff() }
            } else {
                dispatchDiff()
            }

            if (firstVisiblePosition == 0) {
                recyclerView.scrollToPosition(0)
            }
        }

        private fun calculateDiff(newList: List<ListItem>): Pair<DiffUtil.DiffResult, List<ListItem>> {
            val diffResult = DiffUtil.calculateDiff(ListDiffCallback(items, newList), true)
            return diffResult to newList
        }

    }

    private class ListDiffItemCallback<T> : DiffUtil.ItemCallback<ListItem>() {

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

    protected class ListDiffCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return if (oldItem is ListItem && newItem is ListItem) {
                oldItem.listId == newItem.listId
            } else {
                oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition]?.equals(newList[newItemPosition]) ?: false

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldData = oldList[oldItemPosition] as Any
            val newData = newList[newItemPosition] as Any

            return if (newData is ListItem) {
                newData.calculatePayload(oldData)
            } else {
                null
            }
        }
    }
}
