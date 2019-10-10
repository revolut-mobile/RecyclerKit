package com.revolut.recyclerkit.delegates

import androidx.recyclerview.widget.RecyclerView
import java.util.*

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

class DelegatesManager {

    private val delegates = LinkedHashMap<Int, RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>>()

    fun getDelegateFor(viewType: Int): RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder> {
        return delegates[viewType] ?: throw IllegalStateException("No delegate found for viewType $viewType")
    }

    fun getViewTypeFor(position: Int, data: Any): Int {
        for (delegate in delegates.values) {
            if (delegate.suitFor(position, data)) {
                return delegate.viewType
            }
        }
        throw IllegalStateException("No delegate found for position $position and object $data")
    }

    fun addDelegate(delegate: RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>): DelegatesManager {
        delegates[delegate.viewType] = delegate
        return this
    }

    fun getDelegates(): Collection<RecyclerViewDelegate<out ListItem, out RecyclerView.ViewHolder>> {
        return delegates.values
    }

}
