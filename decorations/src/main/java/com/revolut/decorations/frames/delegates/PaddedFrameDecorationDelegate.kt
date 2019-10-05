package com.revolut.decorations.frames.delegates

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.R
import kotlinx.android.parcel.IgnoredOnParcel
import java.util.*

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
class PaddedFrameDecorationDelegate(
    @DimenRes private val topPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val rightPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val bottomPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val leftPadding: Int = R.dimen.zero_padding_decoration,
    @DimenRes private val minHeight: Int = R.dimen.zero_padding_decoration
) : BaseFrameDecorationDelegate() {

    @IgnoredOnParcel
    private val paddedViews = Collections.newSetFromMap(WeakHashMap<View, Boolean>())

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (!paddedViews.contains(view)) {
            val paddingTop = resources.getDimensionPixelSize(topPadding)
            val paddingBottom = resources.getDimensionPixelSize(bottomPadding)
            val paddingLeft = resources.getDimensionPixelSize(leftPadding)
            val paddingRight = resources.getDimensionPixelSize(rightPadding)

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
}