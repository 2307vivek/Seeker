/*
 * Copyright 2023 Vivek Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.vivvvek.seeker

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Stable
class SeekerState() {

    var currentSegment: Segment by mutableStateOf(Segment.Unspecified)

    var onDrag: ((Float) -> Unit)? = null

    internal val draggableState = DraggableState {
        onDrag?.invoke(it)
    }

    internal fun currentSegment(
        value: Float,
        segments: List<Segment>
    ) = (segments.findLast { value >= it.start } ?: Segment.Unspecified).also { this.currentSegment = it }
}

@Composable
fun rememberSeekerState(): SeekerState = remember {
    SeekerState()
}

@Immutable
data class Segment(
    val name: String,
    val start: Float,
    val color: Color = Color.Unspecified
) {
    companion object {
        val Unspecified = Segment(name = "", start = 0f)
    }
}

@Immutable
internal data class SegmentPxs(
    val name: String,
    val startPx: Float,
    val endPx: Float,
    val color: Color
)
