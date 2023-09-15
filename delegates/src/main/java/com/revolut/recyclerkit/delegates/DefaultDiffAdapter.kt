package com.revolut.recyclerkit.delegates

import android.os.Looper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/*
 * Copyright (C) 2023 Revolut
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
*/

/*
* @param autoScrollToTop if autoscroll is true then RecyclerView will be scrolled to zero item on update
* if last zero item was completely visible (i.e. zero scroll).
* @param detectMoves – true if DiffUtil should try to detect moved items, false otherwise.
*/

open class DefaultDiffAdapter(
    delegatesManager: DelegatesManager = DelegatesManager(),
    private val autoScrollToTop: Boolean = false,
    private val detectMoves: Boolean = true,
): AbsRecyclerDelegatesAdapter(delegatesManager) {

    interface ListWrapper<T> : List<T> {
        fun clear()
        operator fun set(index: Int, element: T): T
        fun addAll(elements: Collection<T>): Boolean
    }
    protected class ArrayListListWrapper<T> : ArrayList<T>(), ListWrapper<T>

    private var recyclerView = WeakReference<RecyclerView>(null)
    override val items: ListWrapper<ListItem> = ArrayListListWrapper()

    override fun getItem(position: Int): ListItem = items[position]

    @UiThread
    override fun setItems(items: List<ListItem>) {
        check(Looper.myLooper() == Looper.getMainLooper()) { "DefaultRecyclerDelegatesAdapter.setItems() was called from worker thread" }

        calculateDiff(items).let { (diffResult, newList) ->
            dispatchDiffInternal(diffResult, newList)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = WeakReference(recyclerView)
    }

    protected fun dispatchDiffInternal(diffResult: DiffUtil.DiffResult, newList: List<ListItem>) {
        val rv = recyclerView.get() ?: error("Recycler View not attached")

        val firstVisiblePosition = when (val lm = rv.layoutManager) {
            is LinearLayoutManager -> lm.findFirstCompletelyVisibleItemPosition()
            else -> 0
        }

        val dispatchDiff: () -> Unit = {
            items.clear()
            items.addAll(newList)

            diffResult.dispatchUpdatesTo(this)
        }

        if (rv.isComputingLayout) {
            rv.post { dispatchDiff() }
        } else {
            dispatchDiff()
        }

        if (autoScrollToTop && firstVisiblePosition == 0) {
            rv.scrollToPosition(0)
        }
    }

    protected fun calculateDiff(newList: List<ListItem>): Pair<DiffUtil.DiffResult, List<ListItem>> {
        val diffResult = DiffUtil.calculateDiff(ListDiffCallback(this.items.toList(), newList), detectMoves)
        return diffResult to newList
    }

    private class ListDiffCallback<T>(
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