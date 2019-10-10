package com.revolut.rxdiffadapter

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/*
 * Copyright (C) 2019 Revolut
 * Copyright (C) 2014 The Android Open Source Project
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
*/

/*
 * This RecyclerView passes [RecyclerView.onAttachedToWindow] callbacks
 * to [RxDiffAdapter] to let him dispose async subscriptions.
 */
class AsyncDiffRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onDetachedFromWindow() = super.onDetachedFromWindow()
        .also { (adapter as? RxDiffAdapter)?.onDetachedFromWindow() }

}
