package com.revolut.recyclerkit.sample

import com.revolut.decorations.dividers.delegates.lines.shadows.BottomShadowDecorationDelegate
import com.revolut.decorations.dividers.delegates.lines.shadows.TopShadowDecorationDelegate

object ShadowDecorations {

    val BOTTOM_SHADOW_DECORATION = BottomShadowDecorationDelegate(
        shadow = R.drawable.shadow_cards_bottom
    )

    val TOP_SHADOW_DECORATION = TopShadowDecorationDelegate(
        shadow = R.drawable.shadow_cards_top
    )

}