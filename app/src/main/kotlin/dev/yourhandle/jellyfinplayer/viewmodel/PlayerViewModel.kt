package dev.yourhandle.jellyfinplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.yourhandle.jellyfinplayer.data.JellyfinRepository
import dev.yourhandle.jellyfinplayer.data.model.Item
import dev.yourhandle.jellyfinplayer.player.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentTrack: Item? = null,
    val queue: List<Item> = emptyList(),
    val isPlaying: Boolean = false,
    val isExpanded: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)

class PlayerViewModel(
    private val repository: JellyfinRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun play(track: Item, queue: List<Item>) {
        val streamUrl = repository.getStreamUrl(track.Id ?: return)
        val imageUrl = repository.getImageUrl(track.Id ?: return)
        playerController.play(streamUrl, track.Id ?: return)
        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            queue = queue,
            isPlaying = true,
            isExpanded = true
        )
    }

    fun playPause() {
        playerController.playPause()
        _uiState.value = _uiState.value.copy(
            isPlaying = !_uiState.value.isPlaying
        )
    }

    fun skipNext() {
        val queue = _uiState.value.queue
        val currentId = _uiState.value.currentTrack?.Id
        val currentIndex = queue.indexOfFirst { it.Id == currentId }
        if (currentIndex >= 0 && currentIndex < queue.size - 1) {
            play(queue[currentIndex + 1], queue)
        }
    }

    fun skipPrevious() {
        val queue = _uiState.value.queue
        val currentId = _uiState.value.currentTrack?.Id
        val currentIndex = queue.indexOfFirst { it.Id == currentId }
        if (currentIndex > 0) {
            play(queue[currentIndex - 1], queue)
        }
    }

    fun expandPlayer() {
        _uiState.value = _uiState.value.copy(isExpanded = true)
    }

    fun collapsePlayer() {
        _uiState.value = _uiState.value.copy(isExpanded = false)
    }

    fun getImageUrl(itemId: String?): String? {
        return itemId?.let { repository.getImageUrl(it) }
    }

    class Factory(
        private val repository: JellyfinRepository,
        private val playerController: PlayerController
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerViewModel(repository, playerController) as T
        }
    }
}
