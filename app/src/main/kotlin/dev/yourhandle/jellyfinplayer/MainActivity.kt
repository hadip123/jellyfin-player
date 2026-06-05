package dev.yourhandle.jellyfinplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yourhandle.jellyfinplayer.data.JellyfinRepository
import dev.yourhandle.jellyfinplayer.player.PlayerController
import dev.yourhandle.jellyfinplayer.ui.component.NowPlayingBar
import dev.yourhandle.jellyfinplayer.ui.screen.LibraryScreen
import dev.yourhandle.jellyfinplayer.ui.screen.LoginScreen
import dev.yourhandle.jellyfinplayer.ui.screen.PlayerScreen
import dev.yourhandle.jellyfinplayer.ui.theme.JellyfinPlayerTheme
import dev.yourhandle.jellyfinplayer.viewmodel.LibraryViewModel
import dev.yourhandle.jellyfinplayer.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {

    private lateinit var repository: JellyfinRepository
    private lateinit var playerController: PlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = JellyfinRepository(applicationContext)
        playerController = PlayerController.getInstance(applicationContext)

        setContent {
            JellyfinPlayerTheme {
                JellyfinPlayerApp(repository, playerController)
            }
        }
    }
}

@Composable
private fun JellyfinPlayerApp(
    repository: JellyfinRepository,
    playerController: PlayerController
) {
    val isLoggedIn = remember { mutableStateOf(repository.loadCredentials()) }

    if (!isLoggedIn.value) {
        LoginScreen(
            repository = repository,
            onLoginSuccess = { isLoggedIn.value = true }
        )
    } else {
        MainScreen(repository, playerController)
    }
}

@Composable
private fun MainScreen(
    repository: JellyfinRepository,
    playerController: PlayerController
) {
    val libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModel.Factory(repository)
    )
    val playerViewModel: PlayerViewModel = viewModel(
        factory = PlayerViewModel.Factory(repository, playerController)
    )

    val playerState by playerViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NowPlayingBar(
                visible = playerState.currentTrack != null && !playerState.isExpanded,
                trackTitle = playerState.currentTrack?.Name,
                artist = playerState.currentTrack?.Artist,
                albumArtUrl = playerState.currentTrack?.let { playerViewModel.getImageUrl(it.Id) },
                isPlaying = playerState.isPlaying,
                onPlayPause = { playerViewModel.playPause() },
                onSkipNext = { playerViewModel.skipNext() },
                onSkipPrevious = { playerViewModel.skipPrevious() },
                onTap = { playerViewModel.expandPlayer() }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (playerState.isExpanded) {
                PlayerScreen(
                    viewModel = playerViewModel,
                    onClose = { playerViewModel.collapsePlayer() }
                )
            } else {
                LibraryScreen(
                    viewModel = libraryViewModel,
                    onTrackClick = { track, queue ->
                        playerViewModel.play(track, queue)
                    }
                )
            }
        }
    }
}
