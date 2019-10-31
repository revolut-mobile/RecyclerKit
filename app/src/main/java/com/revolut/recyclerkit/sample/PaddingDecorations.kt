package com.revolut.recyclerkit.sample

import com.revolut.decorations.dividers.delegates.paddings.PaddingDecorationBackground
import com.revolut.decorations.dividers.delegates.paddings.PaddingDecorationDelegate

object PaddingDecorations {

    val PADDING_16DP = PaddingDecorationDelegate(
        padding = R.dimen.dp_16,
        background = PaddingDecorationBackground.ColorResource(R.color.grayTranslucent)
    )

}