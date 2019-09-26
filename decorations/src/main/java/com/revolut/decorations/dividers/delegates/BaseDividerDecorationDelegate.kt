package com.revolut.decorations.dividers.delegates

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

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

abstract class BaseDividerDecorationDelegate : DividerDecorationDelegate {

    private lateinit var contextWeak: WeakReference<Context>

    protected val resources: Resources
        get() = (contextWeak.get() ?: throw IllegalStateException("Context is null")).resources

    protected val context: Context
        get() = (contextWeak.get() ?: throw IllegalStateException("Context is null"))

    @CallSuper
    override fun onDrawOverTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawOverBottom(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawBottom(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun getItemOffsetsTop(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun getItemOffsetsBottom(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawOverLeft(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawOverRight(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawLeft(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun onDrawRight(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun getItemOffsetsLeft(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    @CallSuper
    override fun getItemOffsetsRight(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        contextWeak = WeakReference(parent.context)
    }

    //Actual drawing top of the view
    fun View.translatedTop() = top + translationY

    //Actual drawing bottom of the view
    fun View.translatedBottom() = bottom + translationY

    //Actual drawing bottom of the view
    fun View.translatedRight() = right + translationX

    //Actual drawing bottom of the view
    fun View.translatedLeft() = left + translationX

}