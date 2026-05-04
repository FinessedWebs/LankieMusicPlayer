package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.components.SharedFab
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

// 🔥 helper model
sealed class ArtistListItem {
    data class Header(val title: String) : ArtistListItem()
    data class SongItem(val song: Song, val source: List<Song>) : ArtistListItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistName: String,
    viewModel: PlayerViewModel,
    navController: NavController
) {

    val (mainSongs, collaborations) =
        viewModel.getArtistSongsAdvanced(artistName)

    val currentUri by viewModel.currentSongUri
    val listState = rememberLazyListState()

    // 🔥 BUILD SECTIONED LIST (NO NESTED SCROLL)
    val items = remember(mainSongs, collaborations) {

        buildList<ArtistListItem> {

            if (mainSongs.isNotEmpty()) {
                add(ArtistListItem.Header("Songs"))
                mainSongs.forEach {
                    add(ArtistListItem.SongItem(it, mainSongs))
                }
            }

            if (collaborations.isNotEmpty()) {
                add(ArtistListItem.Header("Collaborations"))
                collaborations.forEach {
                    add(ArtistListItem.SongItem(it, collaborations))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artistName) }
            )
        }
    ) { padding ->



        SongList(
            songs = items.mapNotNull {
                (it as? ArtistListItem.SongItem)?.song
            },
            viewModel = viewModel,
            currentPlayingUri = currentUri,
            listState = listState,
            modifier = Modifier.padding(padding),
            isCollaborationSong = { song ->
                collaborations.contains(song)
            },

            // 🔥 CUSTOM HEADER INJECTION
            trailingText = { song, index ->

                val item = items.getOrNull(index)

                when (item) {
                    is ArtistListItem.Header -> item.title
                    else -> null
                }
            },

            onSongClick = { clicked ->

                val item = items.find {
                    it is ArtistListItem.SongItem && it.song == clicked
                } as? ArtistListItem.SongItem

                val playlist = item?.source ?: mainSongs

                viewModel.playSong(clicked, playlist)
            },

            onNavigateToArtist = { },
            onNavigateToAllSongs = {
                navController.navigate("allsongs/${it.uri}")
            }
        )

        SharedFab(
            mode = FabMode.SHUFFLE,
            listState = listState,
            onShuffle = {
                val all = mainSongs + collaborations
                viewModel.playShuffled(all)
            }
        )
    }
}

