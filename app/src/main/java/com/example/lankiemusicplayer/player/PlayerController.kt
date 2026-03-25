package com.example.lankiemusicplayer.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.guava.await
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.media3.common.MediaMetadata
import com.example.lankiemusicplayer.model.Song

class PlayerController(context: Context) {

    private val _controller = mutableStateOf<MediaController?>(null)
    val controller: State<MediaController?> = _controller

    suspend fun connect(context: Context) {

        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        _controller.value = MediaController.Builder(context, sessionToken)
            .buildAsync()
            .await()
    }

    fun play(song: Song, playlist: List<Song>) {

        val mediaItems = playlist.map {
            MediaItem.Builder()
                .setUri(it.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artist)
                        .setArtworkUri(
                            Uri.parse("content://media/external/audio/albumart/${it.albumId}")
                        )
                        .build()
                )
                .build()
        }

        val startIndex = playlist.indexOf(song).coerceAtLeast(0)

        _controller.value?.apply {
            setMediaItems(mediaItems)
            seekTo(startIndex, 0)   // ⭐ correct approach
            prepare()
            play()
        }
    }

    fun pause() {
        _controller.value?.pause()
    }

    fun createMediaItem(song: Song): MediaItem {

        return MediaItem.Builder()
            .setUri(song.uri)
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setDisplayTitle(song.title)
                    .setArtist(song.artist)
                    .setArtworkUri(
                        Uri.parse("content://media/external/audio/albumart/${song.albumId}")
                    )
                    .build()
            )
            .build()
    }

    fun getPlayer(): MediaController? = controller.value
}