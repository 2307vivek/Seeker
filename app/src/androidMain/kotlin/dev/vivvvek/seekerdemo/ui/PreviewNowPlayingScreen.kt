package dev.vivvvek.seekerdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.vivvvek.seekerdemo.ui.theme.SeekerTheme
import dev.vivvvek.seekerdemo.NowPlayingScreen

@Preview(showBackground = true)
@Composable
fun PreView() {
    SeekerTheme {
        NowPlayingScreen()
    }
}
