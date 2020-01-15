package com.revolut.decorations.overlay.delegates

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

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
data class ColorOverlayDecorationDelegate(
    @ColorRes private val colorRes: Int
) : OverlayDecorationDelegate {

    private val rect = Rect()

    private lateinit var weakResources: WeakReference<Resources>

    private val resources: Resources
        get() = weakResources.get() ?: throw IllegalStateException("Resources is null")

    private val paint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, colorRes, null)
        }
    }

    override fun onDrawOver(canvas: android.graphics.Canvas, parent: RecyclerView, view: android.view.View) {
        weakResources = WeakReference(view.resources)

        parent.getDecoratedBoundsWithMargins(view, rect)

        canvas.drawRect(rect, paint)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColorOverlayDecorationDelegate

        if (colorRes != other.colorRes) return false

        return true
    }

    override fun hashCode(): Int {
        return colorRes
    }

}