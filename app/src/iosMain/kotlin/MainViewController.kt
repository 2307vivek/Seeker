import androidx.compose.ui.window.ComposeUIViewController
import dev.vivvvek.seekerdemo.ui.theme.SeekerTheme
import dev.vivvvek.seekerdemo.NowPlayingScreen

fun MainViewController() = ComposeUIViewController {
    SeekerTheme {
        NowPlayingScreen()
    }
}