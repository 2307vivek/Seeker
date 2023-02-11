package dev.vivvvek.seeker

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test


class SeekerUtilsTest {

    @Test
    fun check_correctPixelValuesForSegments() {
        val state = SeekerState()
        val segments = listOf(
            Segment(name = "Intro", start = 0f),
            Segment(name = "Talk 1", start = 0.5f),
            Segment(name = "Talk 2", start = 0.8f)
        )

        val segmentPxs = state.segmentToPxValues(segments, 0f..1f, 100f, 100f)

        val expected = listOf(
            SegmentPxs(name = "Intro", startPx = 0f, endPx = 50f, color = Color.Unspecified),
            SegmentPxs(name = "Talk 1", startPx = 50f, endPx = 80f, color = Color.Unspecified),
            SegmentPxs(name = "Talk 2", startPx = 80f, endPx = 100f, color = Color.Unspecified)
        )

        assertEquals(expected, segmentPxs)
    }
}