package dev.yourhandle.jellyfinplayer.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerController private constructor(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(url: String, itemId: String) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(itemId)
            .setUri(url)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun release() {
        exoPlayer.release()
    }

    fun getPlayer(): Player = exoPlayer

    companion object {
        @Volatile
        private var instance: PlayerController? = null

        fun getInstance(context: Context): PlayerController {
            return instance ?: synchronized(this) {
                instance ?: PlayerController(context.applicationContext).also { instance = it }
            }
        }
    }
}
