package com.revolut.decorations.frames.delegates

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class BackgroundColorDecoration(
    @ColorRes private val backgroundColor: Int
) : BaseFrameDecorationDelegate() {

    private val rect = Rect()

    private val paint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, backgroundColor, null)
        }
    }

    override fun onDraw(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, view, parent, state)
        parent.getDecoratedBoundsWithMargins(view, rect)

        canvas.drawRect(rect, paint)
    }
}