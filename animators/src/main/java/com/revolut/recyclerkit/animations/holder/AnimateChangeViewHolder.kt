package com.revolut.recyclerkit.animations.holder

import androidx.recyclerview.widget.RecyclerView

/*
 * Copyright (C) 2019 Yatsinar
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

interface AnimateChangeViewHolder {

    /**
     * @return true if this set of payloads can be animated inside onBindViewHolder.
     * false will force RecyclerView to create additional ViewHolder and perform
     * cross-fade between two states.
     */
    fun canAnimateChange(payloads: List<Any>): Boolean

    /**
     * Called whenever RecyclerView needs all animations to stop.
     * It's very important to stop them upon request as detaching Views aren't done
     * during animation and RecyclerView might loose track of attached views if animations aren't finished.
     */
    fun endChangeAnimation(holder: RecyclerView.ViewHolder)

}
