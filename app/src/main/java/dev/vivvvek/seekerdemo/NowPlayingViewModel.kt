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
package dev.vivvvek.seekerdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vivvvek.seeker.Segment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NowPlayingViewModel : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _position = MutableStateFlow(0f)
    val position: StateFlow<Float> = _position

    private val _readAheadPosition = MutableStateFlow(0f)
    val readAheadPosition: StateFlow<Float> = _readAheadPosition

    val length = 145f

    val segments = listOf(
        Segment(name = "Intro", start = 0f),
        Segment(name = "Talking about state", start = 22f),
        Segment(name = "Just architecture things", start = 40f),
        Segment(name = "Jetpack Compose stability", start = 57f),
        Segment(name = "Compose Navigation is shit", start = 100f),
        Segment(name = "Composition phases", start = 123f),
        Segment(name = "Wrapping up", start = 135f),
    )

    private var job: Job? = null

    fun playOrPause() {
        _isPlaying.value = !_isPlaying.value

        if (!_isPlaying.value) {
            job?.cancel()
        } else startPlaying()
    }

    fun setPosition(position: Float) {
        _position.value = position
    }

    fun onPositionChangeFinished() {
        _readAheadPosition.value = _position.value
    }

    private fun startPlaying() {
        job = viewModelScope.launch {
            while (_isPlaying.value && _position.value < length) {
                delay(1000)
                _position.value += 1f
                _readAheadPosition.value += 4f

                if (_position.value == length) {
                    _isPlaying.value = false
                    break
                }
            }
        }
    }
}
