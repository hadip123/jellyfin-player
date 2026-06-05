package dev.yourhandle.jellyfinplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.yourhandle.jellyfinplayer.data.JellyfinRepository
import dev.yourhandle.jellyfinplayer.data.model.Item
import dev.yourhandle.jellyfinplayer.data.model.MediaFolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val libraries: List<MediaFolder> = emptyList(),
    val albums: List<Item> = emptyList(),
    val tracks: List<Item> = emptyList(),
    val currentAlbum: Item? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedAlbumId: String? = null
)

class LibraryViewModel(
    private val repository: JellyfinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getAlbums()
            result.fold(
                onSuccess = { albums ->
                    _uiState.value = _uiState.value.copy(
                        albums = albums,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun selectAlbum(album: Item) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentAlbum = album,
                isLoading = true,
                error = null
            )
            val result = repository.getTracks(album.Id ?: return@launch)
            result.fold(
                onSuccess = { tracks ->
                    _uiState.value = _uiState.value.copy(
                        tracks = tracks,
                        isLoading = false,
                        selectedAlbumId = album.Id
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun getImageUrl(itemId: String?): String? {
        return itemId?.let { repository.getImageUrl(it) }
    }

    fun clearAlbumSelection() {
        _uiState.value = _uiState.value.copy(
            currentAlbum = null,
            tracks = emptyList(),
            selectedAlbumId = null
        )
    }

    class Factory(private val repository: JellyfinRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LibraryViewModel(repository) as T
        }
    }
}
