package com.example.lankiemusicplayer.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log

object ArtworkExtractor {

    fun getArtwork(context: Context, uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()

        return try {

            retriever.setDataSource(context, uri)

            val art = retriever.embeddedPicture

            if (art != null) {
                BitmapFactory.decodeByteArray(art, 0, art.size)
            } else {
                Log.d("LankieArtwork", "No embedded artwork")
                null
            }

        } catch (e: Exception) {

            Log.e("LankieArtwork", "Artwork extraction failed: ${e.message}")
            null

        } finally {

            retriever.release()

        }
    }
}