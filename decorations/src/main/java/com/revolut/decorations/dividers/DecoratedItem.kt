package com.revolut.decorations.dividers

import com.revolut.decorations.dividers.delegates.DividerDecorationDelegate

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

interface DecoratedItem {

    var topDecoration: DividerDecorationDelegate?

    var bottomDecoration: DividerDecorationDelegate?

    var leftDecoration: DividerDecorationDelegate?

    var rightDecoration: DividerDecorationDelegate?

}