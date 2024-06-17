package dev.vivvvek.seeker

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun SeekerPreview() {
    val segments = listOf(
        Segment(name = "Intro", start = 0f),
        Segment(name = "Talk 1", start = 0.5f),
        Segment(name = "Talk 2", start = 0.8f),
    )
    Seeker(
        value = 0.7f,
        range = 0f..1f,
        segments = segments,
        onValueChange = { },
    )
}
