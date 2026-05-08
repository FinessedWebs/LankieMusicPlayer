package com.example.lankiemusicplayer.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.player.PlayerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.model.Playlist
import com.example.lankiemusicplayer.model.YouTubeSong
import com.example.lankiemusicplayer.player.SleepTimerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


class PlayerViewModel(application: Application) : AndroidViewModel(application) {


    private val _youtubeResults = mutableStateListOf<YouTubeSong>()
    val youtubeResults: List<YouTubeSong> get() = _youtubeResults

    private val _isYoutubeLoading = mutableStateOf(false)
    val isYoutubeLoading: State<Boolean> = _isYoutubeLoading
    private val recentlyPlayed = mutableStateListOf<Song>()

    private val playCounts = mutableMapOf<String, Int>()

    private val allPlayedSongs = mutableMapOf<String, Song>()
    val playerController = PlayerController(application)

    // Shared state - survives navigation because ViewModel survives
    private val _currentTitle = mutableStateOf("Nothing playing")
    val currentTitle: State<String> = _currentTitle

    private val _currentArtist = mutableStateOf("")
    val currentArtist: State<String> = _currentArtist

    private val _progress = mutableFloatStateOf(0f)
    val progress: State<Float> = _progress

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    // Track actual time values for wave calculation
    private val _currentPosition = mutableLongStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _duration = mutableLongStateOf(0L)
    val duration: State<Long> = _duration

    // Current song URI for highlighting in list
    private val _currentSongUri = mutableStateOf<String?>(null)
    val currentSongUri: State<String?> = _currentSongUri

    private var currentPlaylist: List<Song> = emptyList()

    private val prefs: SharedPreferences =
        application.getSharedPreferences("lankie_player_prefs", Application.MODE_PRIVATE)

    private val likedSongsKey = "liked_songs"

    private val likedSongs = mutableStateListOf<Long>()
    private var originalPlaylist: List<Song> = emptyList()

    private var currentIndex: Int = 0


    private val playlists = mutableStateListOf<Playlist>()

    private val playlistsKey = "playlists"

    // Derived remaining time in milliseconds
    val remainingMs: Long
        get() = (_duration.longValue - _currentPosition.longValue).coerceAtLeast(0L)
    private val currentSongKey = "current_song_uri"
    private val currentPositionKey = "current_position"
    private val isPlayingKey = "is_playing"

    private val _allSongs = mutableStateListOf<Song>()
    val allSongs: List<Song> get() = _allSongs
    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> = _isShuffleEnabled

    private val _repeatMode = mutableStateOf(RepeatMode.OFF)
    val repeatModeState: State<RepeatMode> = _repeatMode
    private val recentlyPlayedKey = "recently_played"
    private val playCountsKey = "play_counts"


    fun loadSongs(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            val songs = MusicScanner.getSongs(context)

            // Switch to MAIN thread before touching player
            withContext(Dispatchers.Main) {

                _allSongs.clear()
                _allSongs.addAll(songs)

                loadPlayCounts()
                loadRecentlyPlayed()
                restorePlaybackState(context) // ✅ SAFE now
            }
        }
    }

    init {
        loadSongs(application)
        loadLikedSongs()
        loadPlaylists()

        viewModelScope.launch {

            playerController.connect(application)

            val player = playerController.getPlayer()

            player?.addListener(object : androidx.media3.common.Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(
                    mediaItem: androidx.media3.common.MediaItem?,
                    reason: Int
                ) {

                    val uri = mediaItem?.localConfiguration?.uri
                    _currentSongUri.value = uri?.toString()

                    val metadata = mediaItem?.mediaMetadata

                    val newTitle = metadata?.displayTitle?.toString()
                        ?: metadata?.title?.toString()
                        ?: "Nothing playing"

                    val newArtist = metadata?.artist?.toString() ?: ""

                    // 🔥 Update metadata
                    updateMetadata(newTitle, newArtist)

                    val index = currentPlaylist.indexOfFirst { it.uri == uri }

                    if (index != -1) {

                        currentIndex = index

                        val song = currentPlaylist[index]

                        registerPlay(song)
                    }

                    // 🔥 SONG SLEEP TIMER SUPPORT
                    if (
                        reason ==
                        androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
                    ) {

                        SleepTimerManager.onSongCompleted(
                            playerController.getPlayer()
                        )
                    }
                }
            })



            _repeatMode.value = when (player?.repeatMode) {
                androidx.media3.common.Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                androidx.media3.common.Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                else -> RepeatMode.OFF
            }

            startProgressLoop()
          /*  loadPlayCounts()*/

        }
    }

    fun updateMetadata(title: String, artist: String) {
        _currentTitle.value = title
        _currentArtist.value = artist
    }

    private suspend fun startProgressLoop() {
        while (kotlin.coroutines.coroutineContext.isActive) {

            val player = playerController.getPlayer()
            val duration = player?.duration ?: 0L
            val position = player?.currentPosition ?: 0L

            _duration.longValue = duration.coerceAtLeast(0L)
            _currentPosition.longValue = position.coerceAtLeast(0L)

            _currentSongUri.value =
                player?.currentMediaItem?.localConfiguration?.uri?.toString()

            if (duration > 0) {
                _progress.value = position.toFloat() / duration
            }

            // ✅ SAVE STATE HERE WHEN NEEDED (every 2000ms)
            if (_currentSongUri.value != null) {
                savePlaybackState()
            }

            delay(2000) // every 1 second
        }
    }

    fun togglePlayPause() {
        playerController.getPlayer()?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun skipNext() {
        playerController.getPlayer()?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        playerController.getPlayer()?.seekToPreviousMediaItem()
    }

    // ✅ Seek to specific position
    fun seekTo(positionMs: Long) {
        playerController.getPlayer()?.seekTo(positionMs)
    }

    // ✅ Seek by progress (0.0 to 1.0)
    fun seekToProgress(progress: Float) {
        val duration = _duration.longValue
        if (duration > 0) {
            val position = (progress * duration).toLong()
            seekTo(position)
        }
    }

    fun playSong(song: Song, playlist: List<Song>) {

        lastPlayedUri = null

        originalPlaylist = playlist
        currentPlaylist = playlist

        val startIndex = playlist.indexOf(song).coerceAtLeast(0)
        currentIndex = startIndex

        // 🔥 ADD THIS
        updateMetadata(song.title, song.artist)

        playerController.play(song, playlist)

        val key = song.uri.toString()
        allPlayedSongs[key] = song


        registerPlay(song)
    }

    private var lastPlayedUri: String? = null

    private fun registerPlay(song: Song) {

        val key = song.uri.toString()

        // جلوگیری از duplicate count
        if (lastPlayedUri == key) return
        lastPlayedUri = key

        // MOST PLAYED
        playCounts[key] = (playCounts[key] ?: 0) + 1
        allPlayedSongs[key] = song
        savePlayCounts()

        // RECENTLY PLAYED
        recentlyPlayed.removeAll { it.uri == song.uri }
        recentlyPlayed.add(0, song)

        if (recentlyPlayed.size > 15) {
            recentlyPlayed.removeAt(recentlyPlayed.lastIndex)
        }

        saveRecentlyPlayed()
    }

    private fun loadLikedSongs() {

        val stored = prefs.getString(likedSongsKey, "") ?: ""

        if (stored.isNotEmpty()) {

            likedSongs.clear()

            likedSongs.addAll(
                stored.split("|").mapNotNull { it.toLongOrNull() }
            )
        }
    }

    private fun saveLikedSongs() {

        val stored = likedSongs.joinToString("|")

        prefs.edit()
            .putString(likedSongsKey, stored)
            .apply()
    }

    private fun savePlayCounts() {

        val json = JSONObject()

        playCounts.forEach { (uri, count) ->
            json.put(uri, count)
        }

        prefs.edit()
            .putString(playCountsKey, json.toString())
            .apply()
    }

    private fun loadPlayCounts() {

        val stored = prefs.getString(playCountsKey, null) ?: return

        val json = JSONObject(stored)

        playCounts.clear()

        json.keys().forEach { key ->
            playCounts[key] = json.getInt(key)
        }
    }

    private fun saveRecentlyPlayed() {

        val jsonArray = JSONArray()

        recentlyPlayed.forEach { song ->
            jsonArray.put(song.uri.toString())
        }

        prefs.edit()
            .putString(recentlyPlayedKey, jsonArray.toString())
            .apply()
    }

    private fun loadRecentlyPlayed() {

        val stored = prefs.getString(recentlyPlayedKey, null) ?: return

        val jsonArray = JSONArray(stored)

        recentlyPlayed.clear()

        for (i in 0 until jsonArray.length()) {
            val uri = jsonArray.getString(i)

            val song = _allSongs.find { it.uri.toString() == uri }

            if (song != null) {
                recentlyPlayed.add(song)
            }
        }
    }



    fun isLiked(song: Song): Boolean {
        return likedSongs.contains(song.id)
    }

    fun getFavorites(allSongs: List<Song>): List<Song> {

        val map = allSongs.associateBy { it.id }

        return likedSongs.mapNotNull { map[it] }
    }

    fun toggleLike(song: Song) {

        val id = song.id

        if (likedSongs.contains(id)) {
            likedSongs.remove(id)
        } else {
            likedSongs.remove(id)
            likedSongs.add(0, id)
        }

        saveLikedSongs()
    }

    fun getRecentlyPlayed(): List<Song> {
        return recentlyPlayed
    }

    fun getMostPlayed(): List<Pair<Song, Int>> {

        return playCounts
            .entries
            .sortedByDescending { it.value }
            .mapNotNull { entry ->

                val song =
                    allPlayedSongs[entry.key]
                        ?: _allSongs.find { it.uri.toString() == entry.key }

                song?.let { it to entry.value }
            }
    }

    fun getQueue(): List<Song> {

        if (currentPlaylist.isEmpty()) return emptyList()

        return currentPlaylist.drop(currentIndex + 1)
    }

    fun toggleShuffle() {

        _isShuffleEnabled.value = !_isShuffleEnabled.value

        val player = playerController.getPlayer() ?: return

        val currentSong = currentPlaylist.getOrNull(currentIndex) ?: return

        val currentPosition = player.currentPosition

        val originalIndex = originalPlaylist.indexOfFirst { it.uri == currentSong.uri }

        currentPlaylist = if (_isShuffleEnabled.value) {

            val remaining = originalPlaylist
                .filterIndexed { index, _ -> index != originalIndex }
                .shuffled()

            mutableListOf(currentSong).apply {
                addAll(remaining)
            }

        } else {

            originalPlaylist.drop(originalIndex).toMutableList()
        }

        currentIndex = 0

        val mediaItems = currentPlaylist.map {
            playerController.createMediaItem(it)
        }

        player.setMediaItems(mediaItems, 0, currentPosition)
        player.prepare()

        if (_isPlaying.value) {
            player.play()
        }
    }

    /*fun isShuffleEnabled(): Boolean {
        return shuffleEnabled
    }*/

    fun toggleRepeat() {

        val player = playerController.getPlayer() ?: return

        val newMode = when (player.repeatMode) {

            androidx.media3.common.Player.REPEAT_MODE_OFF -> {
                player.repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
                RepeatMode.ALL
            }

            androidx.media3.common.Player.REPEAT_MODE_ALL -> {
                player.repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
                RepeatMode.ONE
            }

            else -> {
                player.repeatMode = androidx.media3.common.Player.REPEAT_MODE_OFF
                RepeatMode.OFF
            }
        }

        _repeatMode.value = newMode
    }

    /*fun getRepeatMode(): RepeatMode {

        val player = playerController.getPlayer() ?: return RepeatMode.OFF

        return when (player.repeatMode) {

            androidx.media3.common.Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            androidx.media3.common.Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            else -> RepeatMode.OFF
        }
    }*/

    fun getCurrentSong(): Song? {

        if (currentPlaylist.isEmpty()) return null

        return currentPlaylist[currentIndex]
    }

    fun getPlaylists(): List<Playlist> {
        return playlists
    }

    fun createPlaylist(name: String) {

        val playlist = Playlist(
            id = System.currentTimeMillis(),
            name = name
        )

        playlists.add(playlist)

        savePlaylists()
    }

    fun getPlaylist(id: Long): Playlist? {
        return playlists.find { it.id == id }
    }

    fun addSongToPlaylist(playlistId: Long, song: Song) {

        val playlist = playlists.find { it.id == playlistId } ?: return

        val id = song.id

        if (!playlist.songs.contains(id)) {

            playlist.songs.add(id)

            savePlaylists()
        }
    }

    fun getPlaylistSongs(
        playlistId: Long,
        allSongs: List<Song>
    ): List<Song> {

        val playlist = playlists.find { it.id == playlistId } ?: return emptyList()

        val map = allSongs.associateBy { it.id }

        return playlist.songs.mapNotNull { map[it] }
    }

    fun playNext(song: Song) {

        val player = playerController.getPlayer() ?: return

        if (currentPlaylist.isEmpty()) return

        // Remove if already in playlist
        val mutable = currentPlaylist.toMutableList()
        mutable.removeAll { it.id == song.id }

        // Insert after current song
        val insertIndex = (currentIndex + 1).coerceAtMost(mutable.size)

        mutable.add(insertIndex, song)

        currentPlaylist = mutable

        val mediaItems = currentPlaylist.map {
            playerController.createMediaItem(it)
        }

        val currentPosition = player.currentPosition

        player.setMediaItems(mediaItems, currentIndex, currentPosition)

        player.prepare()

        if (_isPlaying.value) {
            player.play()
        }
    }

    fun addToQueue(song: Song) {
        val player = playerController.getPlayer() ?: return

        val mediaItem = playerController.createMediaItem(song)

        player.addMediaItem(mediaItem)
    }

    fun playShuffled(songs: List<Song>) {

        if (songs.isEmpty()) return

        val shuffled = songs.shuffled()

        val randomStartIndex = (shuffled.indices).random()

        val startSong = shuffled[randomStartIndex]

        // Rotate list so chosen song is first
        val finalPlaylist = shuffled.drop(randomStartIndex) + shuffled.take(randomStartIndex)

        playSong(startSong, finalPlaylist)
    }

    fun shareSong(context: Context, song: Song) {

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, song.uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share song")
        )
    }

    fun deleteSong(context: Context, song: Song) {
        context.contentResolver.delete(song.uri, null, null)
    }

    private fun savePlaylists() {

        val jsonArray = JSONArray()

        playlists.forEach { playlist ->

            val obj = JSONObject()

            obj.put("id", playlist.id)
            obj.put("name", playlist.name)

            val songs = JSONArray()

            playlist.songs.forEach { songs.put(it) }

            obj.put("songs", songs)

            jsonArray.put(obj)
        }

        prefs.edit()
            .putString(playlistsKey, jsonArray.toString())
            .apply()
    }

    private fun loadPlaylists() {

        val stored = prefs.getString(playlistsKey, null) ?: return

        val jsonArray = JSONArray(stored)

        playlists.clear()

        for (i in 0 until jsonArray.length()) {

            val obj = jsonArray.getJSONObject(i)

            val id = obj.getLong("id")
            val name = obj.getString("name")

            val songsArray = obj.getJSONArray("songs")

            val songs = mutableListOf<Long>()

            for (j in 0 until songsArray.length()) {
                val id = songsArray.getLong(j) // ✅ ALWAYS safe
                songs.add(id)
            }



            playlists.add(
                Playlist(
                    id = id,
                    name = name,
                    songs = songs
                )
            )
        }
    }

    private fun savePlaybackState() {

        val player = playerController.getPlayer() ?: return
        val uri = player.currentMediaItem?.localConfiguration?.uri?.toString()

        prefs.edit()
            .putString(currentSongKey, uri)
            .putLong(currentPositionKey, player.currentPosition)
            .putBoolean(isPlayingKey, player.isPlaying)
            .apply()
    }

    private fun restorePlaybackState(context: Context) {

        val uri = prefs.getString(currentSongKey, null) ?: return
        val position = prefs.getLong(currentPositionKey, 0L)
        val shouldPlay = prefs.getBoolean(isPlayingKey, false)

        val song = _allSongs.find { it.uri.toString() == uri } ?: return

        restoreSong(song, position, shouldPlay)
    }

    private fun restoreSong(song: Song, position: Long, shouldPlay: Boolean) {
        val player = playerController.getPlayer() ?: return

        // restore shared UI state immediately
        _currentSongUri.value = song.uri.toString()
        _currentTitle.value = song.title
        _currentArtist.value = song.artist

        // rebuild queue without treating it like a fresh "play"
        currentPlaylist = _allSongs
        originalPlaylist = _allSongs
        currentIndex = _allSongs.indexOfFirst { it.uri == song.uri }.coerceAtLeast(0)

        val mediaItems = currentPlaylist.map {
            playerController.createMediaItem(it)
        }

        player.setMediaItems(mediaItems, currentIndex, position)
        player.prepare()

        if (shouldPlay) {
            player.play()
        } else {
            player.pause()
        }
    }

    fun removeFromQueue(song: Song) {

        val player = playerController.getPlayer() ?: return

        if (currentPlaylist.isEmpty()) return

        val mutable = currentPlaylist.toMutableList()

        val index = mutable.indexOfFirst { it.id == song.id }

        if (index == -1) return

        mutable.removeAt(index)

        currentPlaylist = mutable

        val mediaItems = currentPlaylist.map {
            playerController.createMediaItem(it)
        }

        val safeIndex = currentIndex.coerceAtMost(currentPlaylist.lastIndex)

        player.setMediaItems(mediaItems, safeIndex, player.currentPosition)
        player.prepare()

        if (_isPlaying.value) {
            player.play()
        }
    }

    fun moveQueueItem(from: Int, to: Int) {

        if (from == to) return
        if (from !in currentPlaylist.indices || to !in currentPlaylist.indices) return

        val mutable = currentPlaylist.toMutableList()

        val item = mutable.removeAt(from)
        mutable.add(to, item)

        currentPlaylist = mutable

        val player = playerController.getPlayer() ?: return

        val mediaItems = currentPlaylist.map {
            playerController.createMediaItem(it)
        }

        player.setMediaItems(mediaItems, currentIndex, player.currentPosition)
        player.prepare()

        if (_isPlaying.value) {
            player.play()
        }
    }

    fun getArtists(): Map<String, List<Song>> {

        if (_allSongs.isEmpty()) return emptyMap()

        return _allSongs
            .groupBy { song ->
                val artist = song.artist.trim()
                if (artist.isBlank()) "Unknown Artist" else artist
            }
            .toSortedMap() // alphabetical
    }

    fun getArtistSongs(artist: String): List<Song> {
        return getArtists()[artist] ?: emptyList()
    }

    fun searchYouTube(query: String) {

        if (query.isBlank()) {
            _youtubeResults.clear()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            _isYoutubeLoading.value = true

            try {
                val apiKey = "AIzaSyCv2keeAq4DGrmH3Z3gJ5ntspwDgbjS3pY"

                val url = """
                https://www.googleapis.com/youtube/v3/search
                ?part=snippet
                &q=${query.replace(" ", "%20")}
                &type=video
                &maxResults=10
                &key=$apiKey
            """.trimIndent()

                val response = java.net.URL(url).readText()
                val json = JSONObject(response)
                val items = json.getJSONArray("items")

                val results = mutableListOf<YouTubeSong>()

                for (i in 0 until items.length()) {

                    val item = items.getJSONObject(i)

                    val videoId = item.getJSONObject("id").getString("videoId")
                    val snippet = item.getJSONObject("snippet")

                    val title = snippet.getString("title")
                    val channel = snippet.getString("channelTitle")
                    val thumbnail = snippet
                        .getJSONObject("thumbnails")
                        .getJSONObject("default")
                        .getString("url")

                    results.add(
                        YouTubeSong(
                            title = title,
                            channel = channel,
                            videoId = videoId,
                            thumbnail = thumbnail
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    _youtubeResults.clear()
                    _youtubeResults.addAll(results)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isYoutubeLoading.value = false
            }

        }

    }

    fun deletePlaylist(playlistId: Long) {
        playlists.removeAll { playlist ->
            playlist.id == playlistId
        }
        savePlaylists()
    }

    fun getArtistSongsAdvanced(artist: String): Pair<List<Song>, List<Song>> {

        val lowerArtist = artist.lowercase()

        val mainSongs = _allSongs.filter {
            it.artist.equals(artist, ignoreCase = true)
        }

        val collaborations = _allSongs.filter { song ->

            val titleMatch = song.title.lowercase().contains(lowerArtist)

            val artistMatch = song.artist.equals(artist, ignoreCase = true)

            titleMatch && !artistMatch
        }

        return mainSongs to collaborations
    }







}

enum class RepeatMode {
    OFF, ALL, ONE
}

