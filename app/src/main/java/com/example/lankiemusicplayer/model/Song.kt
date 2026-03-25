package com.example.lankiemusicplayer.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val uri: Uri,
    val duration: Long,
    val albumId: Long,
    val album: String,
    val dateAdded: Long
)