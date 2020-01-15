package com.revolut.decorations.dividers.delegates.lines.shadows

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.R
import com.revolut.decorations.dividers.delegates.lines.LineDividerDecorationDelegate

/*
 * Copyright (C) 2019 Revolut
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

open class BaseShadowDecorationDelegate protected constructor(
    @DrawableRes protected open val shadow: Int,
    @ColorRes private val backgroundColor: Int = android.R.color.transparent,

    @DimenRes override val dividerHeight: Int = R.dimen.dp_8,
    @DimenRes override val preDividerPadding: Int = R.dimen.dp_16,
    @DimenRes override val postDividerPadding: Int = R.dimen.dp_16
) : LineDividerDecorationDelegate(
    dividerColor = android.R.color.transparent,
    dividerHeight = dividerHeight,
    preDividerPadding = preDividerPadding,
    postDividerPadding = postDividerPadding
) {

    private val shadowDrawable by lazy {
        ResourcesCompat.getDrawable(resources, shadow, null)
    }

    private val backgroundPaint by lazy {
        Paint().apply {
            color = ResourcesCompat.getColor(resources, backgroundColor, null)
        }
    }
    private val maxBackgroundPaintAlpha: Int by lazy {
        backgroundPaint.alpha
    }

    private val shadowRect = Rect()

    override fun onDrawTop(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawTop(canvas, view, parent, state)

        /**
         * View might have alpha 0.0f but be invisible due to RecyclerView animation schedule.
         * This prevents decorators from appearing before View
         */
        if (view.isInvisible) return
        val lm = parent.layoutManager ?: return

        shadowDrawable?.apply {
            alpha = (255 * view.alpha).toInt()

            shadowRect.left = lm.getDecoratedLeft(view)
            shadowRect.top = view.translatedTop().toInt() - postDividerPaddingPx - dividerHeightPx
            shadowRect.right = lm.getDecoratedRight(view)
            shadowRect.bottom = view.translatedTop().toInt() - postDividerPaddingPx

            bounds = shadowRect

            backgroundPaint.alpha = (maxBackgroundPaintAlpha * view.alpha).toInt()
            canvas.drawRect(shadowRect, backgroundPaint)
            draw(canvas)
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

        shadowDrawable?.apply {
            alpha = (255 * view.alpha).toInt()

            shadowRect.left = lm.getDecoratedLeft(view)
            shadowRect.top = view.translatedBottom().toInt() + preDividerPaddingPx
            shadowRect.right = lm.getDecoratedRight(view)
            shadowRect.bottom = view.translatedBottom().toInt() + preDividerPaddingPx + dividerHeightPx

            bounds = shadowRect

            backgroundPaint.alpha = (maxBackgroundPaintAlpha * view.alpha).toInt()
            canvas.drawRect(shadowRect, backgroundPaint)
            draw(canvas)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseShadowDecorationDelegate) return false
        if (!super.equals(other)) return false

        if (shadow != other.shadow) return false
        if (backgroundColor != other.backgroundColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + shadow
        result = 31 * result + backgroundColor
        return result
    }

}