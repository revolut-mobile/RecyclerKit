package com.revolut.rxdiffadapter

import android.animation.ValueAnimator
import android.os.Looper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.revolut.recyclerkit.delegates.DefaultDiffAdapter
import com.revolut.recyclerkit.delegates.DelegatesManager
import com.revolut.recyclerkit.delegates.ListItem
import com.revolut.recyclerkit.delegates.RecyclerViewDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

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
*/

/*
* @param async if async is true then difference between new and old items will be calculated on worker thread, otherwise
* it will be calculated synchronously on the main thread right inside setItems().
* [AsyncDiffRecyclerView] is obligatory for async mode.
* @param autoScrollToTop if autoscroll is true then RecyclerView will be scrolled to zero item on update
* if last zero item was completely visible (i.e. zero scroll).
*/
open class RxDiffAdapter @Deprecated("Replace with constructor without delegates") constructor(
    delegatesManager: DelegatesManager,
    val async: Boolean = false,
    private val autoScrollToTop: Boolean = false,
    private val detectMoves: Boolean = true
) : DefaultDiffAdapter(delegatesManager = delegatesManager, autoScrollToTop = autoScrollToTop, detectMoves = detectMoves) {

    companion object {

        fun setThrowErrorsOnPreconditions(enabled: Boolean) {
            Preconditions.throwErrorsEnabled = enabled
        }
    }

    constructor(
        async: Boolean = false,
        autoScrollToTop: Boolean = false,
        detectMoves: Boolean = true,
        delegates: List<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>
    ) : this(
        async = async,
        autoScrollToTop = autoScrollToTop,
        detectMoves = detectMoves,
        delegatesManager = DelegatesManager(delegates).also { Preconditions.checkForDuplicateDelegates(delegates) }
    )

    private class Queue<T>(
        val processor: PublishProcessor<T>,
        val disposable: Disposable
    )

    private class CopyOnWriteListWrapper<T> : CopyOnWriteArrayList<T>(), ListWrapper<T>

    override val items: ListWrapper<ListItem> = if (async) CopyOnWriteListWrapper() else ArrayListListWrapper()

    private var queue: Queue<List<ListItem>>? = null

    private fun createQueue(): Queue<List<ListItem>> = PublishProcessor.create<List<ListItem>>().let {
        Queue(
            processor = it,
            disposable = it.onBackpressureLatest().throttleLast(ValueAnimator.getFrameDelay(), TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.single())
                .map { newList -> calculateDiff(newList) }
                .observeOn(AndroidSchedulers.mainThread(), false, 1)
                .subscribe { (diffResult, newList) -> dispatchDiffInternal(diffResult, newList) }
        )
    }

    private fun getOrCreateQueue(): Queue<List<ListItem>> =
        queue?.takeUnless { it.disposable.isDisposed } ?: createQueue().apply { queue = this }

    open fun updateItem(index: Int, item: ListItem) {
        if (index < itemCount) {
            this.items[index] = item
            notifyItemChanged(index)
        }
    }

    open fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    @UiThread
    override fun setItems(items: List<ListItem>) {
        check(Looper.myLooper() == Looper.getMainLooper()) { "RxDiffAdapter.setItems() was called from worker thread" }

        if (async) {
            getOrCreateQueue().processor.onNext(items)
        } else {
            calculateDiff(items).let { (diffResult, newList) -> dispatchDiffInternal(diffResult, newList) }
        }
    }

    fun onDetachedFromWindow() = queue?.disposable?.dispose()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        check(!(async && (recyclerView !is AsyncDiffRecyclerView))) { "RxDiffAdapter in async mode must be used with AsyncDiffRecyclerView" }
    }

    override fun getItem(position: Int): ListItem = items[position]

    override fun getItemCount(): Int = items.size
}
