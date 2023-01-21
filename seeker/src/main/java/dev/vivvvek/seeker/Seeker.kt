package dev.vivvvek.seeker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun Seeker(
    range: ClosedFloatingPointRange<Float>,
    value: Float,
    onValueChange: (Float) -> Unit,
    segments: List<Segment> = emptyList()
) {
    
}

@Composable
fun Track(
    range: ClosedFloatingPointRange<Float>,
    segments: List<Segment>,
    modifier: Modifier
) {

    Canvas(modifier = Modifier.fillMaxSize()) {
        segments.forEach {

        }
    }
}

fun DrawScope.segment(
    start: Float,
    end: Float,
    height: Float,
    segmentColor: Color,
    progressColor: Color
) {
    drawLine(
        color = segmentColor,
        start = Offset(start, center.y),
        end = Offset(end, center.y),
        strokeWidth = height
    )
    drawLine(
        color = progressColor,
        start = Offset(start, center.y),
        end = Offset(end, center.y),
        strokeWidth = height
    )
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
