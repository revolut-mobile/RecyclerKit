package com.revolut.decorations.dividers.delegates.lines.shadows

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.revolut.decorations.R

class BottomShadowDecorationDelegate constructor(
    @DrawableRes override val shadow: Int,
    @ColorRes private val backgroundColor: Int = android.R.color.transparent,
    @DimenRes override val dividerHeight: Int = R.dimen.dp_8,
    @DimenRes override val preDividerPadding: Int = R.dimen.dp_16,
    @DimenRes override val postDividerPadding: Int = R.dimen.zero_padding_decoration
) : BaseShadowDecorationDelegate(
    shadow = shadow,
    dividerHeight = dividerHeight,
    backgroundColor = backgroundColor,
    preDividerPadding = preDividerPadding,
    postDividerPadding = postDividerPadding
)