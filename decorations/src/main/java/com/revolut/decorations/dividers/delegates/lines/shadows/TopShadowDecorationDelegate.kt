package com.revolut.decorations.dividers.delegates.lines.shadows

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.revolut.decorations.R

class TopShadowDecorationDelegate constructor(
    @DrawableRes override val shadow: Int,
    @ColorRes private val backgroundColor: Int = android.R.color.transparent,
    @DimenRes override val dividerHeight: Int = R.dimen.dp_8,
    @DimenRes override val postDividerPadding: Int = R.dimen.dp_16
) : BaseShadowDecorationDelegate(
    shadow = shadow,
    backgroundColor = backgroundColor,
    dividerHeight = dividerHeight,
    preDividerPadding = R.dimen.zero_padding_decoration,
    postDividerPadding = postDividerPadding
)

