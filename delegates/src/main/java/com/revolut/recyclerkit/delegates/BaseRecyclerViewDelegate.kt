package com.revolut.recyclerkit.delegates

import androidx.annotation.CallSuper
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

abstract class BaseRecyclerViewDelegate<T : ListItem, VH : BaseRecyclerViewHolder> constructor(
    override val viewType: Int,
    private val rule: (pos: Int, data: Any) -> Boolean
) : RecyclerViewDelegate<T, VH> {

    override fun suitFor(position: Int, data: Any): Boolean = rule(position, data)

    @CallSuper
    override fun onBindViewHolder(holder: VH, data: T, pos: Int, payloads: List<Any>?) {
        holder.lastBoundItem = data
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        //do nothing by default
    }

    override fun onViewAttachedToWindow(holder: VH) {
        //do nothing by default
    }

}

