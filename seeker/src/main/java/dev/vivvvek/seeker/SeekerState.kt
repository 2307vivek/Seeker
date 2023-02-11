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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Stable
class SeekerState() {

    var onDrag: ((Float) -> Unit)? = null

    val draggableState = DraggableState {
        onDrag?.invoke(it)
    }

    // returns the corresponding position in pixels of progress in the the slider.
    internal fun valueToPx(
        value: Float,
        widthPx: Float,
        range: ClosedFloatingPointRange<Float>
    ): Float {
        val rangeSIze = range.endInclusive - range.start
        val p = value.coerceIn(range.start, range.endInclusive)
        val progressPercent = (p - range.start) * 100 / rangeSIze
        return (progressPercent * widthPx / 100)
    }

    // returns the corresponding progress value for a position in slider
    internal fun pxToValue(
        position: Float,
        widthPx: Float,
        range: ClosedFloatingPointRange<Float>
    ): Float {
        val rangeSize = range.endInclusive - range.start
        val percent = position * 100 / widthPx
        return ((percent * (rangeSize) / 100) + range.start).coerceIn(
            range.start,
            range.endInclusive
        )
    }

    // converts the start value of a segment to the corresponding start and end pixel values
    // at which the segment will start and end on the track.
    internal fun segmentToPxValues(
        segments: List<Segment>,
        range: ClosedFloatingPointRange<Float>,
        widthPx: Float,
        trackEnd: Float
    ): List<SegmentPxs> {
        val rangeSize = range.endInclusive - range.start
        val sortedSegments = segments.distinct().sortedBy { it.start }
        val segmentStartPxs = sortedSegments.map { segment ->
            // percent of the start of this segment in the range size
            val percent = (segment.start - range.start) * 100 / rangeSize
            val startPx = percent * widthPx / 100
            startPx
        }

        return sortedSegments.mapIndexed { index, segment ->
            val endPx = if (index != sortedSegments.lastIndex) segmentStartPxs[index + 1] else trackEnd
            SegmentPxs(
                name = segment.name,
                color = segment.color,
                startPx = segmentStartPxs[index],
                endPx = endPx
            )
        }
    }
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
)

@Immutable
internal data class SegmentPxs(
    val name: String,
    val startPx: Float,
    val endPx: Float,
    val color: Color
)
