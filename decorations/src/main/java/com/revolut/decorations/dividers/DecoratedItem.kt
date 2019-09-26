package com.revolut.decorations.dividers

import com.revolut.decorations.dividers.delegates.DividerDecorationDelegate

interface DecoratedItem {

    var topDecoration: DividerDecorationDelegate?

    var bottomDecoration: DividerDecorationDelegate?

    var leftDecoration: DividerDecorationDelegate?

    var rightDecoration: DividerDecorationDelegate?

}