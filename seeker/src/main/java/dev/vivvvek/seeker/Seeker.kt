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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Seeker(
    value: Float,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
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

    Track(
        modifier = Modifier.fillMaxSize(),
        enabled = enabled,
        segments = segments,
        colors = colors,
        dimensions = dimensions
    )
}

@Composable
fun Track(
    modifier: Modifier,
    enabled: Boolean,
    segments: List<Segment>,
    colors: SeekerColors,
    dimensions: SeekerDimensions
) {
    val trackColor = colors.trackColor(enabled)
    val thumbRadius by dimensions.thumbRadius()
    val trackHeight by dimensions.trackHeight()
    var startPx: Float
    var endPx: Float
    Canvas(modifier = modifier) {
        startPx = 0f + thumbRadius.toPx()
        endPx = size.width - thumbRadius.toPx()
        if (segments.isEmpty()) {
            drawLine(
                start = Offset(startPx, center.y),
                end = Offset(endPx, center.y),
                color = trackColor.value,
                strokeWidth = trackHeight.toPx()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeekerPreview() {
    Seeker(
        value = 0f,
        onValueChange = {  },
        modifier = Modifier.fillMaxWidth()
    )
}
