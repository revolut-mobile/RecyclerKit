package com.revolut.decorations.dividers.delegates.paddings

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.dividers.delegates.BaseDividerDecorationDelegate

data class PaddingDecorationDelegate constructor(
    @DimenRes val padding: Int,
    @ColorRes val backgroundColor: Int
) : BaseDividerDecorationDelegate() {

    private val paddingPx by lazy {
        resources.getDimensionPixelSize(padding)
    }

    private val backgroundPaint: Paint? by lazy {
        val paintColor = ResourcesCompat.getColor(
            resources,
            backgroundColor,
            context.theme
        )

        when (val paintAlpha = Color.alpha(paintColor)) {
            0 -> null
            else -> Paint().apply {
                color = paintColor
                alpha = paintAlpha
            }
        }
    }

    override fun getItemOffsetsTop(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsetsTop(outRect, view, parent, state)
        outRect.top += paddingPx
    }

    override fun getItemOffsetsBottom(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsetsBottom(outRect, view, parent, state)
        outRect.bottom += paddingPx
    }

    override fun getItemOffsetsLeft(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsetsLeft(outRect, view, parent, state)
        outRect.left += paddingPx
    }

    override fun getItemOffsetsRight(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsetsRight(outRect, view, parent, state)
        outRect.right += paddingPx
    }

    override fun onDrawTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawTop(canvas, view, parent, state)
        val lm = parent.layoutManager ?: return

        backgroundPaint?.drawPadding(
            view = view,
            canvas = canvas,
            left = lm.getDecoratedLeft(view),
            top = view.translatedTop().toInt() - paddingPx,
            right = lm.getDecoratedRight(view),
            bottom = view.translatedTop().toInt()
        )
    }

    override fun onDrawBottom(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawBottom(canvas, view, parent, state)
        val lm = parent.layoutManager ?: return

        backgroundPaint?.drawPadding(
            view = view,
            canvas = canvas,
            left = lm.getDecoratedLeft(view),
            top = view.translatedBottom().toInt(),
            right = lm.getDecoratedRight(view),
            bottom = view.translatedBottom().toInt() + paddingPx
        )
    }

    override fun onDrawLeft(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawLeft(canvas, view, parent, state)
        backgroundPaint?.drawPadding(
            view = view,
            canvas = canvas,
            left = view.translatedLeft().toInt() - paddingPx,
            top = view.translatedTop().toInt(),
            right = view.translatedLeft().toInt(),
            bottom = view.translatedBottom().toInt()
        )
    }

    override fun onDrawRight(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawRight(canvas, view, parent, state)
        backgroundPaint?.drawPadding(
            view = view,
            canvas = canvas,
            left = view.translatedRight().toInt(),
            top = view.translatedTop().toInt(),
            right = view.translatedRight().toInt() + paddingPx,
            bottom = view.translatedBottom().toInt()
        )
    }

    private fun Paint.drawPadding(
        view: View, canvas: Canvas,
        left: Int, top: Int, right: Int, bottom: Int
    ) = run {
        /**
         * View might have alpha 0.0f but be invisible due to RecyclerView animation schedule.
         * This prevents decorators from appearing before View
         */
        if (view.isInvisible) return
        this.alpha = (255 * view.alpha).toInt()

        val rect = Rect(left, top, right, bottom)

        canvas.drawRect(rect, this)
    }

}