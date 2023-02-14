package dev.vivvvek.seekerdemo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vivvvek.seeker.Segment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NowPlayingViewModel: ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _position = MutableStateFlow(0f)
    val position: StateFlow<Float> = _position

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
        }
        else startPlaying()
    }

    fun setPosition(position: Float) {
        _position.value = position
    }

    private fun startPlaying() {
        job = viewModelScope.launch {
            while (_isPlaying.value && _position.value <= length) {
                delay(1000)

                if (_position.value >= length)
                    _isPlaying.value = false

                _position.value += 1f
            }
        }
    }
}