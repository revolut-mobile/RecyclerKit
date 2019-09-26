package com.revolut.decorations.dividers.delegates.lines

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.R
import com.revolut.decorations.dividers.delegates.BaseDividerDecorationDelegate

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

open class LineDividerDecorationDelegate constructor(
    @ColorRes val dividerColor: Int = R.color.color_light_gray_48,
    @ColorRes private val paddingColor: Int = android.R.color.white,
    @DimenRes open val dividerHeight: Int = R.dimen.line_decoration_height,
    @DimenRes open val preDividerPadding: Int = R.dimen.dp_16,
    @DimenRes open val postDividerPadding: Int = R.dimen.dp_16,
    @DimenRes open val dividerPaddingLeft: Int = R.dimen.zero_padding_decoration,
    @DimenRes open val dividerPaddingRight: Int = R.dimen.zero_padding_decoration,
    open val animate: Boolean = true
) : BaseDividerDecorationDelegate() {

    private val dividerPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, this@LineDividerDecorationDelegate.dividerColor, null)
            isAntiAlias = true
        }
    }

    private val maxDividerPaintAlpha: Int by lazy {
        dividerPaint.alpha
    }

    private val paddingPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, this@LineDividerDecorationDelegate.paddingColor, null)
            isAntiAlias = true
        }
    }

    private val maxPaddingPaintAlpha: Int by lazy {
        paddingPaint.alpha
    }

    protected val dividerHeightPx by lazy {
        resources.getDimensionPixelSize(dividerHeight)
    }

    private val dividerPaddingLeftPx by lazy {
        resources.getDimensionPixelSize(dividerPaddingLeft)
    }

    private val dividerPaddingRightPx by lazy {
        resources.getDimensionPixelSize(dividerPaddingRight)
    }

    protected val preDividerPaddingPx by lazy {
        resources.getDimensionPixelSize(preDividerPadding)
    }

    protected val postDividerPaddingPx by lazy {
        resources.getDimensionPixelSize(postDividerPadding)
    }

    private val overallHeightPx by lazy {
        preDividerPaddingPx + dividerHeightPx + postDividerPaddingPx
    }

    override fun onDrawTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawTop(canvas, view, parent, state)
        /**
         * View might have alpha 0.0f but be invisible due to RecyclerView animation schedule.
         * This prevents decorators from appearing before View
         */
        if (view.isInvisible) return
        val lm = parent.layoutManager ?: return

        if (animate) {
            dividerPaint.alpha = (maxDividerPaintAlpha * view.alpha).toInt()
            paddingPaint.alpha = (maxPaddingPaintAlpha * view.alpha).toInt()
        } else {
            dividerPaint.alpha = 255
            paddingPaint.alpha = 255
        }

        canvas.drawRect(

            lm.getDecoratedLeft(view).toFloat() + dividerPaddingLeftPx,
            (view.translatedTop() - postDividerPaddingPx - dividerHeightPx),
            view.right.toFloat() - dividerPaddingRightPx,
            (view.translatedTop() - postDividerPaddingPx),
            dividerPaint
        )

        if (preDividerPaddingPx != 0) {
            canvas.drawRect(
                lm.getDecoratedLeft(view).toFloat(),
                (view.translatedTop() - overallHeightPx),
                lm.getDecoratedRight(view).toFloat(),
                (view.translatedTop() - postDividerPaddingPx - dividerHeightPx),
                paddingPaint
            )
        }

        if (postDividerPaddingPx != 0) {
            canvas.drawRect(
                lm.getDecoratedLeft(view).toFloat(),
                (view.translatedTop() - postDividerPaddingPx),
                lm.getDecoratedRight(view).toFloat(),
                (view.translatedTop()),
                paddingPaint
            )
        }
    }

    override fun onDrawBottom(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawBottom(canvas, view, parent, state)

        /**
         * View might have alpha 0.0f but be invisible due to RecyclerView animation schedule.
         * This prevents decorators from appearing before View
         */
        if (view.isInvisible) return
        val lm = parent.layoutManager ?: return

        if (animate) {
            dividerPaint.alpha = (maxDividerPaintAlpha * view.alpha).toInt()
            paddingPaint.alpha = (maxPaddingPaintAlpha * view.alpha).toInt()
        } else {
            dividerPaint.alpha = 255
            paddingPaint.alpha = 255
        }

        canvas.drawRect(
            view.left.toFloat(),
            (view.translatedBottom() + preDividerPaddingPx),
            view.right.toFloat(),
            (view.translatedBottom() + preDividerPaddingPx + dividerHeightPx),
            dividerPaint
        )

        if (preDividerPaddingPx != 0) {
            canvas.drawRect(
                lm.getDecoratedLeft(view).toFloat(),
                (view.translatedBottom()),
                lm.getDecoratedRight(view).toFloat(),
                (view.translatedBottom() + preDividerPaddingPx),
                paddingPaint
            )
        }

        if (postDividerPaddingPx != 0) {
            canvas.drawRect(
                lm.getDecoratedLeft(view).toFloat(),
                (view.translatedBottom() + preDividerPaddingPx + dividerHeightPx),
                lm.getDecoratedRight(view).toFloat(),
                (view.translatedBottom() + overallHeightPx),
                paddingPaint
            )
        }
    }

    override fun getItemOffsetsTop(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsetsTop(outRect, view, parent, state)
        outRect.top += overallHeightPx
    }

    override fun getItemOffsetsBottom(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsetsBottom(outRect, view, parent, state)
        outRect.bottom += overallHeightPx
    }

}
