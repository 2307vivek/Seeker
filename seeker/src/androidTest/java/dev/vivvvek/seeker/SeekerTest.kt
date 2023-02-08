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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material.Slider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
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
            Slider(
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
}
