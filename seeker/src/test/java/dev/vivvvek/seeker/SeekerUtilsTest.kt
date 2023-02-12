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

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SeekerUtilsTest {

    private lateinit var state: SeekerState
    private lateinit var segments: List<Segment>

    @Before
    fun setup() {
        state = SeekerState()
    }

    @Test
    fun check_correctPixelValuesForSegments() {

        val segmentPxs = segmentToPxValues(segments, 0f..1f, 100f)

        val expected = listOf(
            SegmentPxs(name = "Intro", startPx = 0f, endPx = 50f, color = Color.Unspecified),
            SegmentPxs(name = "Talk 1", startPx = 50f, endPx = 80f, color = Color.Unspecified),
            SegmentPxs(name = "Talk 2", startPx = 80f, endPx = 100f, color = Color.Unspecified)
        )

        assertEquals(expected, segmentPxs)
    }
}
