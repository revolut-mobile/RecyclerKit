package com.revolut.decorations.frames.delegates

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.R
import java.util.*

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
class RoundCornerFrameDecorationDelegate(
    @DimenRes private val topPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val rightPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val bottomPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val leftPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val roundRadiusRes: Int = R.dimen.dp_8,
    private val shouldOverridePadding: Boolean = true,
    @DimenRes private val minHeight: Int = R.dimen.zero_padding_decoration,
    @DrawableRes private val backgroundRes: Int
) : BaseFrameDecorationDelegate() {

    private val paddedViews = Collections.newSetFromMap(WeakHashMap<View, Boolean>())

    private val rect = RectF()

    private val paint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1 * resources.displayMetrics.density
            color = ResourcesCompat.getColor(resources, R.color.color_light_gray_48, null)
            isAntiAlias = true
        }
    }

    private val radius: Float by lazy {
        resources.getDimensionPixelSize(roundRadiusRes).toFloat()
    }

    override fun onDrawOver(canvas: Canvas, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, view, parent, state)
        rect.left = view.left.toFloat() + 1
        rect.top = view.top.toFloat() - 1
        rect.right = view.right.toFloat() - 1
        rect.bottom = view.bottom.toFloat() + 1

        canvas.drawRoundRect(rect, radius, radius, paint)

        view.background = ResourcesCompat.getDrawable(resources, backgroundRes, view.context.theme)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val paddingTop = resources.getDimensionPixelSize(topPadding)
        val paddingBottom = resources.getDimensionPixelSize(bottomPadding)
        val paddingLeft = resources.getDimensionPixelSize(leftPadding)
        val paddingRight = resources.getDimensionPixelSize(rightPadding)

        if (shouldOverridePadding && !paddedViews.contains(view)) {
            view.setPadding(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
            view.minimumHeight = resources.getDimensionPixelSize(minHeight)
            paddedViews.add(view)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoundCornerFrameDecorationDelegate

        if (topPadding != other.topPadding) return false
        if (rightPadding != other.rightPadding) return false
        if (bottomPadding != other.bottomPadding) return false
        if (leftPadding != other.leftPadding) return false
        if (roundRadiusRes != other.roundRadiusRes) return false
        if (shouldOverridePadding != other.shouldOverridePadding) return false
        if (minHeight != other.minHeight) return false
        if (backgroundRes != other.backgroundRes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topPadding
        result = 31 * result + rightPadding
        result = 31 * result + bottomPadding
        result = 31 * result + leftPadding
        result = 31 * result + roundRadiusRes
        result = 31 * result + shouldOverridePadding.hashCode()
        result = 31 * result + minHeight
        result = 31 * result + backgroundRes
        return result
    }

}