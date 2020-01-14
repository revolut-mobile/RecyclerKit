package com.revolut.decorations.frames

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.forEachBaseViewHolder
import com.revolut.recyclerkit.delegates.BaseRecyclerViewHolder

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
class DelegatesFrameItemDecoration : RecyclerView.ItemDecoration() {

    private val rect = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)

        canvas.save()
        rect.set(parent.paddingLeft, parent.paddingTop, parent.width - parent.paddingRight, parent.height - parent.paddingBottom)
        canvas.clipRect(rect)

        parent.forEachBaseViewHolder { vh ->
            (vh.lastBoundItem as? FrameDecoratedItem)?.run {
                frameDecoration?.onDraw(canvas, vh.itemView, parent, state)
            }
        }
        canvas.restore()
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        canvas.save()
        rect.set(parent.paddingLeft, parent.paddingTop, parent.width - parent.paddingRight, parent.height - parent.paddingBottom)
        canvas.clipRect(rect)

        parent.forEachBaseViewHolder { vh ->
            (vh.lastBoundItem as? FrameDecoratedItem)?.run {
                frameDecoration?.onDrawOver(canvas, vh.itemView, parent, state)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val item = (parent.findContainingViewHolder(view) as? BaseRecyclerViewHolder)?.lastBoundItem ?: return
        if (item is FrameDecoratedItem) {
            item.frameDecoration?.getItemOffsets(outRect, view, parent, state)
        }
    }

}
