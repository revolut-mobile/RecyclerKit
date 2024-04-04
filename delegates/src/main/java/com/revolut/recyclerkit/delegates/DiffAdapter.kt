package com.revolut.recyclerkit.delegates

import android.annotation.SuppressLint
import android.os.Looper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
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

    protected interface DifferDelegate {
        val items: List<ListItem>
        fun attachRecyclerView(recyclerView: RecyclerView)
        fun detachRecyclerView(recyclerView: RecyclerView)
        fun setItems(items: List<ListItem>)
    }

    private val differDelegate = if (async) {
        AsyncDifferStrategy(adapter = this, autoScrollToTop = autoScrollToTop)
    } else {
        SyncDifferStrategy(adapter = this, autoScrollToTop = autoScrollToTop, detectMoves = true)
    }

    open val items: List<ListItem>
        get() = differDelegate.items

    override fun getItem(position: Int): ListItem = items[position]

    @UiThread
    open fun setItems(items: List<ListItem>) {
        check(Looper.myLooper() == Looper.getMainLooper()) { "DiffAdapter.setItems() was called from worker thread" }
        differDelegate.setItems(items)
    }

    override fun getItemCount() = items.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        differDelegate.attachRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        differDelegate.detachRecyclerView(recyclerView)
    }

    private class AsyncDifferStrategy(
        adapter: RecyclerView.Adapter<*>,
        private val autoScrollToTop: Boolean
    ) : DifferDelegate {
        private val differ: AsyncListDiffer<ListItem> = AsyncListDiffer(
            AdapterListUpdateCallback(adapter),
            AsyncDifferConfig.Builder(ListDiffItemCallback<ListItem>()).build()
        )
        private var recyclerView = WeakReference<RecyclerView>(null)
        override val items: List<ListItem>
            get() = differ.currentList

        override fun attachRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = WeakReference(recyclerView)
        }

        override fun detachRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = WeakReference(null)
        }

        override fun setItems(items: List<ListItem>) {
            val recyclerViewRef = recyclerView
            val rv = recyclerViewRef.get() ?: error("Recycler View not attached")

            val firstVisiblePosition = rv.layoutManager.findFirstCompletelyVisibleItemPosition(autoScrollToTop)

            if (firstVisiblePosition == 0) {
                differ.submitList(items) {
                    recyclerViewRef.get()?.scrollToPosition(0)
                }
            } else {
                differ.submitList(items)
            }
        }
    }

    protected open class SyncDifferStrategy(
        private val adapter: RecyclerView.Adapter<*>,
        private val autoScrollToTop: Boolean,
        private val detectMoves: Boolean,
    ) : DifferDelegate {
        protected var recyclerView = WeakReference<RecyclerView>(null)
            private set
        override val items = mutableListOf<ListItem>()

        private var lastDispatchDiffCallback: Runnable? = null

        override fun attachRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = WeakReference(recyclerView)
        }

        override fun detachRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = WeakReference(null)
        }

        override fun setItems(items: List<ListItem>) {
            val diffResult = calculateDiff(items)
            dispatchDiffInternal(diffResult, items, recyclerView.get() ?: error("Recycler View not attached"))
        }

        protected fun dispatchDiffInternal(diffResult: DiffUtil.DiffResult, newList: List<ListItem>, recyclerView: RecyclerView) {
            val firstVisiblePosition = recyclerView.layoutManager.findFirstCompletelyVisibleItemPosition(autoScrollToTop)

            val dispatchDiff: () -> Unit = {
                lastDispatchDiffCallback = null
                items.clear()
                items.addAll(newList)

                diffResult.dispatchUpdatesTo(adapter)

                if (firstVisiblePosition == 0) {
                    recyclerView.scrollToPosition(0)
                }
            }

            lastDispatchDiffCallback?.let {
                recyclerView.removeCallbacks(it)
            }
            if (recyclerView.isComputingLayout) {
                val newDispatchDiffCallback = Runnable { dispatchDiff() }
                lastDispatchDiffCallback = newDispatchDiffCallback
                recyclerView.post(newDispatchDiffCallback)
            } else {
                dispatchDiff()
            }
        }

        protected fun calculateDiff(newList: List<ListItem>): DiffUtil.DiffResult {
            return DiffUtil.calculateDiff(ListDiffCallback(ArrayList(items), newList), detectMoves)
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

private fun LayoutManager?.findFirstCompletelyVisibleItemPosition(autoScrollToTop: Boolean): Int = if (autoScrollToTop) {
    when (this) {
        is LinearLayoutManager -> findFirstCompletelyVisibleItemPosition()
        else -> 0
    }
} else {
    -1
}
