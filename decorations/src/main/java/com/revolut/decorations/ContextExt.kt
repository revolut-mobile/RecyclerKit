package com.revolut.decorations

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

internal fun Context.resolveColorAttribute(@AttrRes attrRes: Int) = TypedValue().apply {
    theme.resolveAttribute(attrRes, this, true)
}.resourceId