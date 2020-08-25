package com.revolut.recyclerkit.delegates

import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView

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

class DelegatesManager(
    delegates: List<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>? = null
) {

    private val delegates = SparseArrayCompat<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>()

    init {
        delegates?.let {
            addDelegates(it)
        }
    }

    fun getDelegateFor(viewType: Int): RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder> {
        return delegates[viewType] ?: error("No delegate found for viewType $viewType")
    }

    fun getViewTypeFor(position: Int, data: Any): Int {
        for (index in 0 until delegates.size()) {
            val delegate = delegates.valueAt(index)
            if (delegate.suitFor(position = position, data = data)) {
                return delegate.viewType
            }
        }
        error("No delegate found for position $position and object $data ")
    }

    fun addDelegate(delegate: RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>): DelegatesManager {
        delegates.put(delegate.viewType, delegate)
        return this
    }

    fun addDelegates(delegates: List<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>): DelegatesManager {
        delegates.forEach { addDelegate(it) }
        return this
    }

    fun getDelegates(): Collection<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>> {
        val delegateValues = mutableListOf<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>()
        for (index in 0 until delegates.size()) {
            val delegate = delegates.valueAt(index)
            delegateValues.add(delegate)
        }
        return delegateValues
    }

}