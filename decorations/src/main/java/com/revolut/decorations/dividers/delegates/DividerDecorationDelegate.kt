package com.revolut.decorations.dividers.delegates

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/*
 * Copyright (C) 2019 Yatsinar
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
 */

interface DividerDecorationDelegate {

    fun onDrawTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) = Unit
    fun onDrawBottom(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) = Unit
    fun onDrawLeft(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) = Unit
    fun onDrawRight(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) = Unit

    fun onDrawOverTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State)
    fun onDrawOverBottom(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State)
    fun onDrawOverLeft(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State)
    fun onDrawOverRight(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State)

    fun getItemOffsetsTop(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    fun getItemOffsetsBottom(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    fun getItemOffsetsLeft(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    fun getItemOffsetsRight(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
}