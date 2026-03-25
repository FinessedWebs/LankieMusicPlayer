package com.example.lankiemusicplayer.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.lankiemusicplayer.model.Song

object MusicScanner {

    fun getSongs(context: Context): List<Song> {

        val seenUris = mutableSetOf<String>()

        val songs = mutableListOf<Song>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATE_ADDED
        )

        // Only real music files
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,   // FIXED (was null before)
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {

            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val seenIds = mutableSetOf<Long>()
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)


            while (it.moveToNext()) {

                val size = it.getLong(sizeColumn)

                if (size < 1000) continue

                val id = it.getLong(idColumn)

                // prevent duplicates
                if (seenIds.contains(id)) continue
                seenIds.add(id)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val duration = it.getLong(durationColumn)
                val albumId = it.getLong(albumIdColumn)
                val album = it.getString(albumColumn)
                val dateAdded = it.getLong(dateAddedColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val uriString = contentUri.toString()

                Log.d(
                    "LankieArtworkDebug",
                    "Song: $title | Artist: $artist | AlbumId: $albumId | Uri: $contentUri"
                )

                if (seenUris.contains(uriString)) continue
                seenUris.add(uriString)

                Log.d("LankieMusic", "Song detected: $title")

                songs.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        uri = contentUri,
                        duration = duration,
                        albumId = albumId,
                        album = album,
                        dateAdded = dateAdded
                    )
                )

            }
        }

        Log.d("LankieMusic", "Total songs found: ${songs.size}")

        return songs
    }
}