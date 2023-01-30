package dev.vivvvek.seeker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Seeeker(
    range: ClosedFloatingPointRange<Float>,
    value: Float,
    onValueChange: (Float) -> Unit,
    segments: List<Segment> = emptyList()
) {
    segments.forEach {
        require(it.start in range) {
            "segments must lie in the range."
        }
    }
}

@Composable
fun Track(
    range: ClosedFloatingPointRange<Float>,
    segments: List<Segment>,
    segmentColor: Color,
    progressColor: Color,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.graphicsLayer { alpha = 0.99f }) {
        val startPxs = segmentStartToPx(range, segments.toMutableList(), size.width)
        repeat(startPxs.size) { i ->
            val end = if (i == startPxs.lastIndex)
                size.width
            else
                startPxs[i + 1] - 4.dp.toPx()
            segment(
                start = startPxs[i],
                end = end,
                progressEnd = 0f,
                segmentColor = Color.Gray,
                progressColor = progressColor
            )
        }
//        drawLine(
//            color = Color.Red,
//            start = Offset(0f, center.y),
//            end = Offset(600f, center.y),
//            strokeWidth = 4.dp.toPx(),
//            blendMode = BlendMode.SrcIn
//        )
//        drawLine(
//            color = Color.Blue,
//            start = Offset(0f, center.y),
//            end = Offset(550f, center.y),
//            strokeWidth = 4.dp.toPx(),
//            blendMode = BlendMode.SrcIn
//        )
    }
}

fun DrawScope.segment(
    start: Float,
    end: Float,
    progressEnd: Float,
    height: Float = size.height,
    segmentColor: Color,
    progressColor: Color
) {
    drawLine(
        color = segmentColor,
        start = Offset(start, center.y),
        end = Offset(end, center.y),
        strokeWidth = height,
    )
}

// convert the start of a segment to the px value at which the segment will start.
private fun segmentStartToPx(
    range: ClosedFloatingPointRange<Float>,
    segments: MutableList<Segment>,
    width: Float
): List<Float> {
    if (segments.first().start != range.start) {
        segments.add(0, Segment("", range.start))
    }
    val rangeSize = range.endInclusive - range.start
    val startPxs = segments.map { segment ->
        // percent of the start of this segment in the range size
        val percent = (segment.start - range.start) * 100 / rangeSize
        val startPx = percent * width / 100
        startPx
    }
    return startPxs.distinct().sorted()
}

data class Segment(
    val name: String,
    val start: Float
)

object SeekerDefaults {
    val height = 4.dp
    val trackColor = Color.Gray
    val progressHeight = Color.Red
}

@Preview(showBackground = true)
@Composable
fun PreviewTrack() {
    val segments = listOf(
        Segment(name = "", start = 60f),
        Segment(name = "", start = 70f),
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Track(
            range = 20f..100f,
            segments = segments,
            segmentColor = Color.Gray,
            progressColor = Color.Red,
            progress = 0f,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(4.dp)
        )
    }
}

val colors = listOf(Color.Red, Color.Gray, Color.Green, Color.Blue)
