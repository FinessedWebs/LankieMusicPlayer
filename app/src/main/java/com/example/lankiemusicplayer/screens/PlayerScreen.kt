package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lankiemusicplayer.components.SongOptionsSheet
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import com.example.lankiemusicplayer.viewmodel.RepeatMode
import kotlin.math.roundToInt
import androidx.compose.foundation.combinedClickable

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit = {},
    onShowQueue: () -> Unit = {},
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAllSongs: (String) -> Unit
) {

    val title by viewModel.currentTitle
    val artist by viewModel.currentArtist
    val progress by viewModel.progress
    val isPlaying by viewModel.isPlaying
    val currentPosition by viewModel.currentPosition
    val duration by viewModel.duration

    val isShuffleOn by viewModel.isShuffleEnabled
    val repeatMode by viewModel.repeatModeState

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(0f) }
    var dragPositionMs by remember { mutableStateOf(0L) }

    val currentUri by viewModel.currentSongUri
    val player = viewModel.playerController.getPlayer()


    var showOptions by remember { mutableStateOf(false) }
    val currentSong = viewModel.getCurrentSong()
    val isLiked = currentSong?.let { viewModel.isLiked(it) } ?: false


    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = onShowQueue) {
                Icon(
                    imageVector = Icons.Outlined.QueueMusic,
                    contentDescription = "Queue"
                )
            }
        }

        Spacer(Modifier.height(32.dp))


        // Album artwork
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.CenterHorizontally)
                .combinedClickable(
                    onClick = {
                        currentSong?.let {
                            onNavigateToAllSongs(it.uri.toString())
                        }

                    },
                    onLongClick = {
                        showOptions = true
                    }
                )
        ) {

            ElevatedCard(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                ),

                colors = CardDefaults.elevatedCardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            )  {

                val context = LocalContext.current
                val artworkUri = remember(currentUri) {
                    player?.currentMediaItem?.mediaMetadata?.artworkUri
                }

                var artworkFailed by remember(artworkUri) { mutableStateOf(false) }

                /* Log only when artwork changes */
                LaunchedEffect(artworkUri) {
                    Log.d("LankieArtwork", "Artwork URI changed: $artworkUri")
                }

                if (artworkUri != null && !artworkFailed) {

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(artworkUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Album cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.QueueMusic,
                            contentDescription = "No artwork",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Title + artist + like button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = {
                    currentSong?.let {
                        viewModel.toggleLike(it)
                    }
                }
            ) {

                if (isLiked) {

                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Unlike",
                        tint = MaterialTheme.colorScheme.primary
                    )

                } else {

                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Progress section
        Column(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .pointerInput(Unit) {

                        detectHorizontalDragGestures(

                            onDragStart = { offset ->

                                isDragging = true
                                val width = size.width.toFloat()
                                dragProgress = (offset.x / width).coerceIn(0f, 1f)
                                dragPositionMs = (dragProgress * duration).toLong()
                            },

                            onHorizontalDrag = { change, _ ->

                                val width = size.width.toFloat()
                                dragProgress = (change.position.x / width).coerceIn(0f, 1f)
                                dragPositionMs = (dragProgress * duration).toLong()
                                change.consume()
                            },

                            onDragEnd = {

                                viewModel.seekToProgress(dragProgress)
                                isDragging = false
                            },

                            onDragCancel = {
                                isDragging = false
                            }
                        )
                    }
            ) {

                LinearWavyProgressIndicator(
                    progress = {
                        if (isDragging) dragProgress else progress
                    },
                    amplitude = { _ ->
                        if ((if (isDragging) dragProgress else progress) >= 0.9f) 1f else 0f
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = if (isDragging) formatTime(dragPositionMs) else formatTime(currentPosition),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
            // ✅ Drag tooltip showing minutes
            if (isDragging) {
                Surface(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${formatTime(dragPositionMs)} / ${formatTime(duration)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    viewModel.toggleShuffle()
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffleOn)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            // Previous
            IconButton(
                onClick = { viewModel.skipPrevious() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }


            FilledIconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier.size(82.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Next
            IconButton(
                onClick = { viewModel.skipNext() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = {
                    viewModel.toggleRepeat()
                }
            ) {
                Icon(
                    imageVector = if (repeatMode == RepeatMode.ONE)
                        Icons.Default.RepeatOne
                    else
                        Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.OFF)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    if (showOptions && currentSong != null) {
        SongOptionsSheet(
            song = currentSong,
            viewModel = viewModel,
            onDismiss = { showOptions = false },
            onNavigateToArtist = onNavigateToArtist,
            onNavigateToAllSongs = { song ->
                onNavigateToAllSongs(song.uri.toString())
            }
        )
    }

}

