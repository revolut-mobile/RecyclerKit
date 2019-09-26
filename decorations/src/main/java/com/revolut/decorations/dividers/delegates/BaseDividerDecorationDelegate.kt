package com.revolut.decorations.dividers.delegates

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

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