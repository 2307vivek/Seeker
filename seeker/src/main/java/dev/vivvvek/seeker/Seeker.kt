/*
 * Copyright 2021 Vivek Singh
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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset

@Composable
fun Seeker(
    progress: Float,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: (() -> Unit)? = null,
    segments: List<Segment> = emptyList(),
    enabled: Boolean = true,
    colors: SeekerColors = SeekerDefaults.seekerColors(),
    dimensions: SeekerDimensions = SeekerDefaults.seekerDimensions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier
) {
    segments.forEach {
        require(it.start in range) {
            "segment must start from withing the range."
        }
    }

    BoxWithConstraints {
        val thumbRadius by dimensions.thumbRadius()
        val startPx = thumbRadius
        val endPx = constraints.maxWidth.toFloat()
        val widthPx: Float

        with(LocalDensity.current) {
            widthPx = endPx - startPx.toPx()
        }

        Box(
            modifier = modifier.defaultSeekerDimensions(dimensions),
            contentAlignment = Alignment.CenterStart
        ) {
            Track(
                modifier = Modifier.fillMaxSize(),
                enabled = enabled,
                segments = segments,
                colors = colors,
                dimensions = dimensions
            )
            val progressPx = progressPx(range, widthPx, progress)
            Spacer(
                modifier = Modifier
                    .offset {
                        IntOffset(x = progressPx.toInt(), 0)
                    }
                    .size(dimensions.thumbRadius().value)
                    .clip(CircleShape)
                    .background(colors.thumbColor(enabled = enabled).value)
            )
        }
    }
}

@Composable
private fun Track(
    modifier: Modifier,
    enabled: Boolean,
    segments: List<Segment>,
    colors: SeekerColors,
    dimensions: SeekerDimensions
) {
    val trackColor by colors.trackColor(enabled)
    val thumbRadius by dimensions.thumbRadius()
    val trackHeight by dimensions.trackHeight()

    var endPx: Float
    var startPx: Float

    Canvas(
        modifier = modifier
    ) {
        startPx = thumbRadius.toPx()
        endPx = size.width - thumbRadius.toPx()

        if (segments.isEmpty()) {
            drawSegment(
                startPx = startPx,
                endPx = endPx,
                trackColor = trackColor,
                trackHeight = trackHeight.toPx()
            )
        } else {

        }
    }
}

fun DrawScope.drawSegment(
    startPx: Float,
    endPx: Float,
    trackColor: Color,
    trackHeight: Float
) {
    drawLine(
        start = Offset(startPx, center.y),
        end = Offset(endPx, center.y),
        color = trackColor,
        strokeWidth = trackHeight
    )
}

private fun Modifier.defaultSeekerDimensions(dimensions: SeekerDimensions) = composed {
    with(dimensions) {
        Modifier
            .heightIn(
                max = (thumbRadius().value * 2).coerceAtLeast(SeekerDefaults.MinSliderHeight)
            )
            .widthIn(
                min = SeekerDefaults.MinSliderWidth
            )
    }
}

fun progressPx(range: ClosedFloatingPointRange<Float>, widthPx: Float, progress: Float) : Float {
    val rangeSIze = range.endInclusive - range.start
    val progressPercent = (progress - range.start) * 100 / rangeSIze
    return (progressPercent * widthPx / 100)
}

@Preview(showBackground = true)
@Composable
fun SeekerPreview() {
    Seeker(
        progress = 1f,
        onProgressChange = { },
    )
}
