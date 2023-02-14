package dev.vivvvek.seekerdemo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import dev.vivvvek.seeker.Segment
import dev.vivvvek.seeker.rememberSeekerState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NowPlayingScreen() {

    val viewModel: NowPlayingViewModel = viewModel()
    val position by viewModel.position.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val seekerState = rememberSeekerState()

    val range = 0f..viewModel.length

    Scaffold(
        topBar = { TopBar() }
    ) {
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(56.dp)
                    .background(
                        color = MaterialTheme.colors.surface.copy(0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            Text(
                text = "Podcast #1",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "By several people",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.button
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(formatSeconds(position), style = MaterialTheme.typography.caption)
                        Text(
                            formatSeconds(viewModel.length),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
                Seeker(
                    value = position,
                    onValueChange = viewModel::setPosition,
                    range = range,
                    state = seekerState,
                    segments = viewModel.segments,
                    colors = SeekerDefaults.seekerColors(
                        trackColor = Color(0xFF22233b),
                        progressColor = Color.White,
                        thumbColor = Color.White
                    )
                )
                CurrentSegment(
                    currentSegment = seekerState.currentSegment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colors.surface.copy(0.03f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                )
            }
            Controls(isPlaying = isPlaying, onPlayPause = { viewModel.playOrPause() })
        }
    }
}

@Composable
fun Controls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.round_skip_previous_24), contentDescription = "" )
        }
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .requiredSize(72.dp)
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24
                ),
                contentDescription = "",
                tint = Color.Black
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.round_skip_next_24), contentDescription = "")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CurrentSegment(
    currentSegment: Segment,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = currentSegment,
            transitionSpec = {
                if (targetState.start > initialState.start) {
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }
        ) { currentSegment ->
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = currentSegment.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.round_keyboard_arrow_down_24),
                    contentDescription = ""
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_more_vert_24),
                    contentDescription = ""
                )
            }
        },
        title = {
            Text(
                text = "Now playing",
                fontWeight = FontWeight.Bold
            )
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

fun formatSeconds(seconds: Float): String {
    val minutes = (seconds / 60).toInt()
    val remaining = (seconds % 60).toInt()
    return "$minutes:$remaining"
}

@Preview(showBackground = true)
@Composable
fun PreView() {
    NowPlayingScreen()
}