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

import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object SeekerDefaults {

    @Composable
    fun seekerColors(
        progressColor: Color = MaterialTheme.colors.primary,
        trackColor: Color = progressColor.copy(alpha = TrackAlpha),
        disabledProgressColor: Color = MaterialTheme.colors.onSurface.copy(alpha = DisabledProgressAlpha),
        disabledTrackColor: Color = disabledProgressColor.copy(alpha = DisabledTrackAlpha),
        thumbColor: Color = MaterialTheme.colors.primary,
        disabledThumbColor: Color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
        readAheadColor: Color = Color.White.copy(alpha = ContentAlpha.medium)
    ): SeekerColors = DefaultSeekerColor(
        progressColor = progressColor,
        trackColor = trackColor,
        disabledProgressColor = disabledProgressColor,
        disabledTrackColor = disabledTrackColor,
        thumbColor = thumbColor,
        disabledThumbColor = disabledThumbColor,
        readAheadColor = readAheadColor
    )

    @Composable
    fun gap(gap: Dp = 4.dp) : State<Dp> = rememberUpdatedState(gap)

    private const val TrackAlpha = 0.24f
    private const val DisabledTrackAlpha = 0.12f
    private const val DisabledProgressAlpha = 0.32f
}

@Stable
interface SeekerColors {

    @Composable
    fun trackColor(enabled: Boolean): State<Color>

    @Composable
    fun thumbColor(enabled: Boolean): State<Color>

    @Composable
    fun progressColor(enabled: Boolean): State<Color>

    @Composable
    fun readAheadColor(enabled: Boolean): State<Color>
}

@Immutable
internal class DefaultSeekerColor(
    val progressColor: Color,
    val trackColor: Color,
    val disabledTrackColor: Color,
    val disabledProgressColor: Color,
    val thumbColor: Color,
    val disabledThumbColor: Color,
    val readAheadColor: Color
) : SeekerColors {
    @Composable
    override fun trackColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) trackColor else disabledTrackColor
        )
    }

    @Composable
    override fun thumbColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) thumbColor else disabledThumbColor
        )
    }

    @Composable
    override fun progressColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) progressColor else disabledProgressColor
        )
    }

    @Composable
    override fun readAheadColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            readAheadColor
        )
    }
}
