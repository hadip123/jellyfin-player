package dev.yourhandle.jellyfinplayer.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.yourhandle.jellyfinplayer.ui.component.BlurredAlbumBackground
import dev.yourhandle.jellyfinplayer.ui.theme.IceBlue
import dev.yourhandle.jellyfinplayer.ui.theme.LightGray
import dev.yourhandle.jellyfinplayer.ui.theme.NearBlack
import dev.yourhandle.jellyfinplayer.ui.theme.PureWhite
import dev.yourhandle.jellyfinplayer.viewmodel.PlayerViewModel

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.uiState
    val track = state.currentTrack
    val imageUrl = track?.let { viewModel.getImageUrl(it.Id) }

    BlurredAlbumBackground(
        imageUrl = imageUrl,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            NearBlack.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))

                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(280.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                )

                Spacer(Modifier.height(40.dp))

                Text(
                    text = track?.Name ?: "No track playing",
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = track?.Artist ?: track?.AlbumArtist ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = track?.Album ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                Slider(
                    value = 0f,
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        thumbColor = PureWhite,
                        activeTrackColor = PureWhite,
                        inactiveTrackColor = LightGray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(0),
                        style = MaterialTheme.typography.labelSmall,
                        color = LightGray
                    )
                    Text(
                        text = formatTime(track?.RunTimeTicks),
                        style = MaterialTheme.typography.labelSmall,
                        color = LightGray
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { viewModel.skipPrevious() }) {
                        Icon(
                            imageVector = Icons.Rounded.SkipPrevious,
                            contentDescription = "Previous",
                            tint = PureWhite,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(Modifier.width(24.dp))

                    IconButton(
                        onClick = { viewModel.playPause() },
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(8.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = IceBlue,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    Spacer(Modifier.width(24.dp))

                    IconButton(onClick = { viewModel.skipNext() }) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = "Next",
                            tint = PureWhite,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))
            }
        }
    }
}

private fun formatTime(ticks: Long?): String {
    if (ticks == null) return "--:--"
    val totalSeconds = ticks / 10_000_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
