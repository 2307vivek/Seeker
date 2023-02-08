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

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material.Slider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeekerTest {
    private val tag = "seeker"

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun check_seekerMinSize() {
        rule.setContent {
            Box(modifier = Modifier.requiredSize(0.dp)) {
                Slider(
                    value = 0f,
                    onValueChange = { },
                    modifier = Modifier.testTag(tag)
                )
            }
        }
        rule.onNodeWithTag(tag)
            .assertWidthIsEqualTo(SeekerDefaults.ThumbRadius * 2)
            .assertHeightIsEqualTo(SeekerDefaults.ThumbRadius * 2)
    }

    @Test
    fun check_seekerSizes() {
        rule.setContent {
            Box(
                modifier = Modifier.requiredSizeIn(
                    maxWidth = 100.dp,
                    maxHeight = 100.dp
                )
            ) {
                Slider(
                    value = 0f,
                    onValueChange = { },
                    modifier = Modifier.testTag(tag)
                )
            }
        }
        rule.onNodeWithTag(tag)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(SeekerDefaults.MinSliderHeight)
    }

    @Test
    fun seeker_noUnwantedOnValueChangeCalls() {
        val seekerValue = mutableStateOf(0f)
        val callCount = mutableStateOf(0)

        rule.setContent {
            Seeker(
                value = seekerValue.value,
                onValueChange = {
                    seekerValue.value = it
                    callCount.value += 1
                },
                modifier = Modifier.testTag(tag)
            )
        }

        rule.runOnIdle {
            assertEquals(0, callCount.value)
        }

        rule.onNodeWithTag(tag).performTouchInput {
            down(center)
            up()
        }

        rule.runOnIdle {
            assertEquals(1, callCount.value)
        }
    }

    @Test
    fun seeker_valueChangeFinished_calledOnce() {
        val seekerValue = mutableStateOf(0f)
        val callCount = mutableStateOf(0)

        rule.setContent {
            Seeker(
                modifier = Modifier.testTag(tag),
                value = seekerValue.value,
                onValueChangeFinished = {
                    callCount.value += 1
                },
                onValueChange = { seekerValue.value = it }
            )
        }

        rule.runOnIdle {
            assertEquals(0, callCount.value)
        }

        rule.onNodeWithTag(tag).performTouchInput {
            down(center)
            moveBy(Offset(50f, 50f))
            up()
        }

        rule.runOnIdle {
            assertEquals(1, callCount.value)
        }
    }

    @Test
    fun seeker_semanticsSetProgress_callsOnValueChangeFinished() {
        val state = mutableStateOf(0f)
        val callCount = mutableStateOf(0)

        rule.setContent {
            Seeker(
                modifier = Modifier.testTag(tag),
                value = state.value,
                onValueChangeFinished = {
                    callCount.value += 1
                },
                onValueChange = { state.value = it }
            )
        }

        rule.runOnIdle {
            assertEquals(0, callCount.value)
        }

        rule.onNodeWithTag(tag)
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.8f) }

        rule.runOnIdle {
            assertEquals(1, callCount.value)
        }
    }

    @Test
    fun sliderPosition_value() {
        val seekerValue = mutableStateOf(0f)

        rule.setContent {
            Seeker(
                modifier = Modifier.testTag(tag),
                value = seekerValue.value,
                onValueChange = { seekerValue.value = it },
                range = 0f..1f
            )
        }
        rule.runOnIdle {
            seekerValue.value = 2f
        }
        rule.onNodeWithTag(tag).assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f, 0))

        rule.runOnIdle {
            seekerValue.value = -25451f
        }
        rule.onNodeWithTag(tag).assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f, 0))
    }

    @Test
    fun seeker_checkSemantics() {
        val seekerValue = mutableStateOf(0f)

        rule.setContent {
            Seeker(
                value = seekerValue.value,
                onValueChange = { seekerValue.value = it }
            )

            rule.onNodeWithTag(tag)
                .assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f, 0))
                .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))

            rule.runOnIdle {
                seekerValue.value = 0.5f
            }

            rule.onNodeWithTag(tag)
                .assertRangeInfoEquals(ProgressBarRangeInfo(0.5f, 0f..1f, 0))

            rule.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.SetProgress) { it(0.8f) }

            rule.onNodeWithTag(tag)
                .assertRangeInfoEquals(ProgressBarRangeInfo(0.8f, 0f..1f, 0))
        }
    }

    @Test
    fun seeker_semantics_focusable() {
        rule.setContent {
            Seeker(value = 0f, onValueChange = {}, modifier = Modifier.testTag(tag))
        }

        rule.onNodeWithTag(tag)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Focused))
    }

    @Test
    fun seeker_semantics_disabled() {
        rule.setContent {
            Seeker(
                value = 0f,
                onValueChange = {},
                modifier = Modifier.testTag(tag),
                enabled = false
            )
        }

        rule.onNodeWithTag(tag)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
    }

    @Test
    fun seeker_checkDragInteraction() {
        val seekerValue = mutableStateOf(0f)
        val range = 0f..1f

        rule.setContent {
            Seeker(
                modifier = Modifier.testTag(tag),
                value = seekerValue.value,
                range = range,
                onValueChange = { seekerValue.value = it }
            )
        }

        rule.runOnUiThread {
            assertEquals(0f, seekerValue.value)
        }

        var expected = 0f

        rule.onNodeWithTag(tag)
            .performTouchInput {
                down(center)
                moveBy(Offset(100f, 0f))
                up()
                expected = calculateFraction(left, right, centerX + 100f)
            }
        rule.runOnIdle {
            assertEquals(expected, seekerValue.value, 0.001f)
        }
    }

    @Test
    fun seeker_drag_out_of_bounds() {
        val seekerValue = mutableStateOf(0f)

        rule.setContent {
            Seeker(
                modifier = Modifier.testTag(tag),
                value = seekerValue.value,
                onValueChange = { seekerValue.value = it }
            )
        }

        rule.runOnUiThread {
            assertEquals(0f, seekerValue.value)
        }

        var expected = 0f

        rule.onNodeWithTag(tag)
            .performTouchInput {
                down(center)
                moveBy(Offset(width.toFloat(), 0f))
                moveBy(Offset(-width.toFloat(), 0f))
                moveBy(Offset(-width.toFloat(), 0f))
                moveBy(Offset(width.toFloat() + 100f, 0f))
                up()
                expected = calculateFraction(left, right, centerX + 100)
            }
        rule.runOnIdle {
            assertEquals(expected, seekerValue.value, 0.001f)
        }
    }

    @Test
    fun seeker_scrollableContainer() {
        val seekerValue = mutableStateOf(0f)
        val offset = mutableStateOf(0f)

        rule.setContent {
            Column(
                modifier = Modifier
                    .height(2000.dp)
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { delta ->
                            offset.value += delta
                            delta
                        })
            ) {
                Seeker(
                    modifier = Modifier.testTag(tag),
                    value = seekerValue.value,
                    onValueChange = { seekerValue.value = it }
                )
            }
        }

        rule.runOnIdle {
            assertEquals(0f, seekerValue.value)
        }

        // Just scroll
        rule.onNodeWithTag(tag, useUnmergedTree = true)
            .performTouchInput {
                down(Offset(centerX, centerY))
                moveBy(Offset(0f, 500f))
                up()
            }

        rule.runOnIdle {
            assertTrue(offset.value > 0f)
            assertEquals(0f, seekerValue.value)
        }

        // Tap
        var expected = 0f
        rule.onNodeWithTag(tag, useUnmergedTree = true)
            .performTouchInput {
                click(Offset(centerX, centerY))
                expected = calculateFraction(left, right, centerX)
            }

        rule.runOnIdle {
            assertEquals(expected, seekerValue.value, 0.001f)
        }
    }

    @Test
    fun slider_tap_rangeChange() {
        val seekerValue = mutableStateOf(0f)
        val rangeEnd = mutableStateOf(0.25f)

        rule.setContent {
            Seeker(
                modifier = Modifier.testTag(tag),
                value = seekerValue.value,
                onValueChange = { seekerValue.value = it },
                range = 0f..rangeEnd.value
            )
        }
        // change to 1 since [calculateFraction] coerces between 0..1
        rule.runOnUiThread {
            rangeEnd.value = 1f
        }

        var expected = 0f

        rule.onNodeWithTag(tag)
            .performTouchInput {
                down(Offset(centerX + 50, centerY))
                up()
                expected = calculateFraction(left, right, centerX + 50)
            }

        rule.runOnIdle {
            assertEquals(expected, seekerValue.value, 0.001f)
        }
    }

    private fun calculateFraction(left: Float, right: Float, pos: Float) = with(rule.density) {
        val offset = SeekerDefaults.ThumbRadius.toPx()
        val start = left + offset
        val end = right - offset

        ((pos - start) / (end - start)).coerceIn(0f, 1f)
    }
}
