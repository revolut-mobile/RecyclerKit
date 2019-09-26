package com.revolut.decorations.dividers

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.revolut.recyclerkit.delegates.AbsRecyclerDelegatesAdapter

class DelegatesDividerItemDecoration : RecyclerView.ItemDecoration() {

    private val rect = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        val childCount = parent.childCount
        val adapter = parent.adapter as? AbsRecyclerDelegatesAdapter ?: return

        canvas.save()
        rect.set(parent.paddingLeft, parent.paddingTop, parent.width - parent.paddingRight, parent.height - parent.paddingBottom)
        canvas.clipRect(rect)

        (0 until childCount).forEach {
            val view = parent.getChildAt(it)
            val pos = parent.getChildAdapterPosition(view)
            if (pos == RecyclerView.NO_POSITION) {
                return
            }

            (adapter.getItem(pos) as? DecoratedItem)?.run {
                topDecoration?.onDrawTop(canvas, view, parent, state)
                bottomDecoration?.onDrawBottom(canvas, view, parent, state)
                leftDecoration?.onDrawLeft(canvas, view, parent, state)
                rightDecoration?.onDrawRight(canvas, view, parent, state)
            }
        }
        canvas.restore()
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val childCount = parent.childCount
        val adapter = parent.adapter as? AbsRecyclerDelegatesAdapter ?: return

        canvas.save()
        rect.set(parent.paddingLeft, parent.paddingTop, parent.width - parent.paddingRight, parent.height - parent.paddingBottom)
        canvas.clipRect(rect)

        (0 until childCount).forEach {
            val view = parent.getChildAt(it)
            val pos = parent.getChildAdapterPosition(view)
            if (pos == RecyclerView.NO_POSITION) {
                return
            }

            (adapter.getItem(pos) as? DecoratedItem)?.run {
                topDecoration?.onDrawOverTop(canvas, view, parent, state)
                bottomDecoration?.onDrawOverBottom(canvas, view, parent, state)
                leftDecoration?.onDrawOverLeft(canvas, view, parent, state)
                rightDecoration?.onDrawOverRight(canvas, view, parent, state)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as? AbsRecyclerDelegatesAdapter ?: return
        val pos = parent.getChildAdapterPosition(view)
        if (pos == RecyclerView.NO_POSITION) {
            return
        }

        (adapter.getItem(pos) as? DecoratedItem)?.run {
            topDecoration?.getItemOffsetsTop(outRect, view, parent, state)
            bottomDecoration?.getItemOffsetsBottom(outRect, view, parent, state)
            leftDecoration?.getItemOffsetsLeft(outRect, view, parent, state)
            rightDecoration?.getItemOffsetsRight(outRect, view, parent, state)
        }
    }

}