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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Seeker(
    modifier: Modifier = Modifier,
    state: SeekerState = rememberSeekerState(),
    value: Float,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    readAheadValue: Float = range.start,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    segments: List<Segment> = emptyList(),
    enabled: Boolean = true,
    colors: SeekerColors = SeekerDefaults.seekerColors(),
    dimensions: SeekerDimensions = SeekerDefaults.seekerDimensions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    if (segments.isNotEmpty()) {
        require(segments.first().start == range.start) {
            "the first segment should start from range start value"
        }
        segments.forEach {
            require(it.start in range) {
                "segment must start from withing the range."
            }
        }
    }

    val onValueChangeState by rememberUpdatedState(onValueChange)

    BoxWithConstraints(
        modifier = modifier
            .requiredSizeIn(
                minHeight = SeekerDefaults.ThumbRippleRadius * 2,
                minWidth = SeekerDefaults.ThumbRippleRadius * 2
            )
            .progressSemantics(value, range, onValueChange, onValueChangeFinished, enabled)
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

        val segmentStarts = remember(segments, range, widthPx, trackEnd) {
            state.segmentToPxValues(segments, range, widthPx, trackEnd)
        }

        val rawValuePx = remember(value, widthPx, range) {
            state.valueToPx(value, widthPx, range)
        }
        val valuePx = if (isRtl) -rawValuePx else rawValuePx

        val rawReadAheadValuePx = remember(readAheadValue, widthPx, range) {
            state.valueToPx(readAheadValue, widthPx, range)
        }
        val readAheadValuePx = if (isRtl) -rawReadAheadValuePx else rawReadAheadValuePx

        var dragPositionX by remember { mutableStateOf(0f) }
        var pressOffset by remember { mutableStateOf(0f) }

        val scope = rememberCoroutineScope()

        val draggableState = state.draggableState

        LaunchedEffect(widthPx, range) {
            state.onDrag = {
                dragPositionX += it + pressOffset

                pressOffset = 0f
                onValueChangeState(state.pxToValue(dragPositionX, widthPx, range))
            }
        }

        val press =
            Modifier.pointerInput(
                range,
                widthPx,
                endPx,
                isRtl,
                enabled,
                thumbRadius,
                interactionSource
            ) {
                detectTapGestures(
                    onPress = { position ->
                        dragPositionX = 0f
                        pressOffset = if (!isRtl) position.x - trackStart else (endPx - position.x) - trackStart
                    },
                    onTap = {
                        scope.launch {
                            draggableState.drag(MutatePriority.UserInput) {
                                dragBy(0f)
                            }
                        }
                        onValueChangeFinished?.invoke()
                    }
                )
            }

        val drag = Modifier.draggable(
            state = draggableState,
            reverseDirection = isRtl,
            orientation = Orientation.Horizontal,
            onDragStopped = {
                onValueChangeFinished?.invoke()
            },
            interactionSource = interactionSource
        )

        Seeker(
            modifier = if (enabled) press.then(drag) else Modifier,
            widthPx = widthPx,
            valuePx = valuePx,
            readAheadValuePx = readAheadValuePx,
            enabled = enabled,
            segments = segmentStarts,
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
    valuePx: Float,
    readAheadValuePx: Float,
    enabled: Boolean,
    segments: List<SegmentPxs>,
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
            valuePx = valuePx,
            readAheadValuePx = readAheadValuePx,
            dimensions = dimensions
        )
        Thumb(
            valuePx = { valuePx },
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
    segments: List<SegmentPxs>,
    colors: SeekerColors,
    widthPx: Float,
    valuePx: Float,
    readAheadValuePx: Float,
    dimensions: SeekerDimensions
) {
    val trackColor by colors.trackColor(enabled)
    val progressColor by colors.progressColor(enabled)
    val readAheadColor by colors.readAheadColor(enabled)
    val thumbRadius by dimensions.thumbRadius()
    val trackHeight by dimensions.trackHeight()

    Canvas(
        modifier = modifier.graphicsLayer {
            alpha = 0.99f
        }
    ) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val left = thumbRadius.toPx()
        val right = left + widthPx

        val startPx = if (isRtl) right else left
        val endPx = if (isRtl) left else right

        if (segments.isEmpty()) {
            // draw the track with a single line.
            drawLine(
                start = Offset(startPx, center.y),
                end = Offset(endPx, center.y),
                color = trackColor,
                strokeWidth = trackHeight.toPx(),
                cap = StrokeCap.Round
            )
        } else {
            // segmentToStartPx()
        }

        // readAhead indicator
        drawLine(
            start = Offset(startPx, center.y),
            end = Offset(startPx + readAheadValuePx, center.y),
            color = readAheadColor,
            strokeWidth = trackHeight.toPx(),
            blendMode = BlendMode.Src,
            cap = StrokeCap.Round
        )

        // progress indicator
        drawLine(
            start = Offset(startPx, center.y),
            end = Offset(startPx + valuePx, center.y),
            color = progressColor,
            strokeWidth = trackHeight.toPx(),
            blendMode = BlendMode.Src,
            cap = StrokeCap.Round
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
    valuePx: () -> Float,
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
            .absoluteOffset {
                IntOffset(x = valuePx().toInt(), 0)
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

private fun Modifier.progressSemantics(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean
): Modifier {
    val coerced = value.coerceIn(range.start, range.endInclusive)
    return semantics {
        if (!enabled) disabled()
        setProgress { targetValue ->
            val newValue = targetValue.coerceIn(range.start, range.endInclusive)

            if (newValue == coerced) {
                false
            } else {
                onValueChange(newValue)
                onValueChangeFinished?.invoke()
                true
            }
        }
    }.progressSemantics(value, range, 0)
}

@Preview(showBackground = true)
@Composable
fun SeekerPreview() {
    Seeker(
        value = 60f,
        range = 20f..100f,
        onValueChange = { },
    )
}
