package dev.vivvvek.seekerdemo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.vivvvek.seekerdemo.ui.theme.SeekerTheme

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Seeker",
        ) {
            SeekerTheme {
                NowPlayingScreen()
            }
        }
    }
}