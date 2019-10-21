package com.revolut.decorations.overlay

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import com.revolut.decorations.forEachViewAdapterPosition
import com.revolut.recyclerkit.delegates.AbsRecyclerDelegatesAdapter

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
class DelegatesOverlayItemDecoration : RecyclerView.ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as? AbsRecyclerDelegatesAdapter ?: return

        parent.forEachViewAdapterPosition { view, pos ->
            val item = adapter.getItem(pos)

            if (item is OverlayDecoratedItem) {
                item.overlayColorDecoration?.onDrawOver(canvas, parent, view)
            }
        }
    }

}

