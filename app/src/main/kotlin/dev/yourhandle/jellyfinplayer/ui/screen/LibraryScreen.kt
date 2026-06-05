package dev.yourhandle.jellyfinplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.yourhandle.jellyfinplayer.data.model.Item
import dev.yourhandle.jellyfinplayer.ui.theme.IceBlue
import dev.yourhandle.jellyfinplayer.ui.theme.LightGray
import dev.yourhandle.jellyfinplayer.ui.theme.NearBlack
import dev.yourhandle.jellyfinplayer.ui.theme.PureWhite
import dev.yourhandle.jellyfinplayer.ui.theme.SurfaceCard
import dev.yourhandle.jellyfinplayer.ui.component.TrackRow
import dev.yourhandle.jellyfinplayer.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onTrackClick: (Item, List<Item>) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBlack)
    ) {
        if (state.currentAlbum != null) {
            AlbumDetailView(
                album = state.currentAlbum!!,
                tracks = state.tracks,
                albumArtUrl = viewModel.getImageUrl(state.currentAlbum!!.Id),
                viewModel = viewModel,
                onTrackClick = onTrackClick,
                onBack = { viewModel.clearAlbumSelection() }
            )
        } else {
            AlbumGrid(
                albums = state.albums,
                isLoading = state.isLoading,
                getImageUrl = { viewModel.getImageUrl(it) },
                onAlbumClick = { viewModel.selectAlbum(it) }
            )
        }
    }
}

@Composable
private fun AlbumGrid(
    albums: List<Item>,
    isLoading: Boolean,
    getImageUrl: (String?) -> String?,
    onAlbumClick: (Item) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.displayLarge,
            color = PureWhite,
            modifier = Modifier.padding(16.dp)
        )
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IceBlue)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums, key = { it.Id ?: it.Name ?: "" }) { album ->
                    AlbumCard(
                        album = album,
                        imageUrl = getImageUrl(album.Id),
                        onClick = { onAlbumClick(album) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: Item,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = album.Name ?: "Unknown Album",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = album.AlbumArtist ?: album.Artist ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AlbumDetailView(
    album: Item,
    tracks: List<Item>,
    albumArtUrl: String?,
    viewModel: LibraryViewModel,
    onTrackClick: (Item, List<Item>) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = album.Name ?: "Album",
            style = MaterialTheme.typography.headlineMedium,
            color = PureWhite,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(tracks, key = { it.Id ?: it.Name ?: "" }) { track ->
                TrackRow(
                    track = track,
                    isPlaying = false,
                    imageUrl = viewModel.getImageUrl(track.Id),
                    modifier = Modifier.clickable {
                        onTrackClick(track, tracks)
                    }
                )
            }
        }
    }
}
