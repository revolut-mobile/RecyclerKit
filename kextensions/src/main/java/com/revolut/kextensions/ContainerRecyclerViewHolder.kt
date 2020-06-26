package com.revolut.kextensions

import android.view.View
import com.revolut.recyclerkit.delegates.BaseRecyclerViewHolder
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer

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

// Optimization for Android
@ContainerOptions(cache = CacheImplementation.SPARSE_ARRAY)
abstract class ContainerRecyclerViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView), LayoutContainer {

    override val containerView: View = itemView

}