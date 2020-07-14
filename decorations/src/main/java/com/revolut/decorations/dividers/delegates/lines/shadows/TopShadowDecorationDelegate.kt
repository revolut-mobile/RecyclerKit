package com.revolut.decorations.dividers.delegates.lines.shadows

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.revolut.decorations.R

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

class TopShadowDecorationDelegate constructor(
    @DrawableRes shadow: Int,
    @ColorRes backgroundColor: Int = android.R.color.transparent,
    @ColorRes private val paddingColor: Int = android.R.color.white,
    @DimenRes dividerHeight: Int = R.dimen.dp_8,
    @DimenRes postDividerPadding: Int = R.dimen.dp_16
) : BaseShadowDecorationDelegate(
    shadow = shadow,
    backgroundColor = backgroundColor,
    paddingColor = paddingColor,
    dividerHeight = dividerHeight,
    preDividerPadding = R.dimen.zero_padding_decoration,
    postDividerPadding = postDividerPadding
)

