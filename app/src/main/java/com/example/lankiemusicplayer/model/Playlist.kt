package com.example.lankiemusicplayer.model

data class Playlist(
    val id: Long,
    val name: String,
    val songs: MutableList<Long> = mutableListOf()
)