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
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun Seeker(
    modifier: Modifier = Modifier,
    progress: Float,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: (() -> Unit)? = null,
    segments: List<Segment> = emptyList(),
    enabled: Boolean = true,
    colors: SeekerColors = SeekerDefaults.seekerColors(),
    dimensions: SeekerDimensions = SeekerDefaults.seekerDimensions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    segments.forEach {
        require(it.start in range) {
            "segment must start from withing the range."
        }
    }

    val onProgressChangeState by rememberUpdatedState(onProgressChange)

    BoxWithConstraints(
        modifier = modifier
            .requiredSizeIn(
                minHeight = SeekerDefaults.ThumbRippleRadius * 2,
                minWidth = SeekerDefaults.ThumbRippleRadius * 2
            )
            .focusable(enabled, interactionSource)
    ) {
        val thumbRadius by dimensions.thumbRadius()
        val trackStart: Float
        val endPx = constraints.maxWidth.toFloat()
        val widthPx: Float
        val trackEnd: Float

        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

        with(LocalDensity.current) {
            trackStart = thumbRadius.toPx()
            widthPx = endPx - (trackStart * 2)
            trackEnd = trackStart + widthPx
        }
        val progressPx = progressPx(range, widthPx, progress)

        val gestures =
            Modifier.pointerInput(widthPx, endPx, isRtl, thumbRadius, interactionSource) {
                detectTapGestures(
                    onPress = { position ->
                        if (enabled) {
                            val positionX = if (!isRtl) position.x - trackStart else (endPx - position.x) - trackStart

                            onProgressChangeState(pxToProgress(range, widthPx, positionX, trackStart, trackEnd))
                            onProgressChangeFinished?.invoke()
                        }
                    }
                )
            }

        Seeker(
            modifier = modifier.then(gestures),
            widthPx = widthPx,
            progressPx = progressPx,
            enabled = enabled,
            segments = segments,
            colors = colors,
            dimensions = dimensions,
            interactionSource = interactionSource
        )
    }
}

@Composable
private fun Seeker(
    modifier: Modifier,
    widthPx: Float,
    progressPx: Float,
    enabled: Boolean,
    segments: List<Segment>,
    colors: SeekerColors,
    dimensions: SeekerDimensions,
    interactionSource: MutableInteractionSource
) {
    Box(
        modifier = modifier.defaultSeekerDimensions(dimensions),
        contentAlignment = Alignment.CenterStart
    ) {
        Track(
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,
            segments = segments,
            colors = colors,
            widthPx = widthPx,
            progressPx = progressPx,
            dimensions = dimensions
        )
        Thumb(
            progressPx = progressPx,
            dimensions = dimensions,
            colors = colors,
            enabled = enabled,
            interactionSource = interactionSource
        )
    }
}

@Composable
private fun Track(
    modifier: Modifier,
    enabled: Boolean,
    segments: List<Segment>,
    colors: SeekerColors,
    widthPx: Float,
    progressPx: Float,
    dimensions: SeekerDimensions
) {
    val trackColor by colors.trackColor(enabled)
    val progressColor by colors.progressColor(enabled)
    val thumbRadius by dimensions.thumbRadius()
    val trackHeight by dimensions.trackHeight()

    var endPx: Float
    var startPx: Float

    Canvas(
        modifier = modifier
    ) {
        startPx = thumbRadius.toPx()
        endPx = startPx + widthPx

        if (segments.isEmpty()) {
            drawSegment(
                startPx = startPx,
                endPx = endPx,
                trackColor = trackColor,
                trackHeight = trackHeight.toPx()
            )
        } else {
        }

        drawLine(
            start = Offset(startPx, center.y),
            end = Offset(startPx + progressPx, center.y),
            color = progressColor,
            strokeWidth = trackHeight.toPx(),
            blendMode = BlendMode.SrcIn
        )
    }
}

private fun DrawScope.drawSegment(
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

@Composable
private fun Thumb(
    progressPx: Float,
    dimensions: SeekerDimensions,
    colors: SeekerColors,
    enabled: Boolean,
    interactionSource: MutableInteractionSource
) {
    val interactions = remember { mutableStateListOf<Interaction>() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    val elevation = if (interactions.isEmpty()) {
        SeekerDefaults.ThumbDefaultElevation
    } else {
        SeekerDefaults.ThumbPressedElevation
    }

    Spacer(
        modifier = Modifier
            .offset {
                IntOffset(x = progressPx.toInt(), 0)
            }
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = SeekerDefaults.ThumbRippleRadius
                )
            )
            .hoverable(interactionSource)
            .size(dimensions.thumbRadius().value * 2)
            .clip(CircleShape)
            .shadow(if (enabled) elevation else 0.dp, shape = CircleShape)
            .background(colors.thumbColor(enabled = enabled).value)
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

// returns the corresponding pixel value of progress in the the slider.
private fun progressPx(
    range: ClosedFloatingPointRange<Float>,
    widthPx: Float,
    progress: Float
): Float {
    val rangeSIze = range.endInclusive - range.start
    val p = progress.coerceIn(range.start, range.endInclusive)
    val progressPercent = (p - range.start) * 100 / rangeSIze
    return (progressPercent * widthPx / 100)
}

private fun pxToProgress(
    range: ClosedFloatingPointRange<Float>,
    widthPx: Float,
    pixel: Float,
    startPx: Float,
    endPx: Float
): Float {
    val rangeSize = range.endInclusive - range.start
    val px = pixel.coerceIn(startPx, endPx)
    val percent = px * 100 / widthPx
    return percent * rangeSize / 100
}

@Preview(showBackground = true)
@Composable
fun SeekerPreview() {
    Seeker(
        progress = 0.4f,
        onProgressChange = { },
    )
}
