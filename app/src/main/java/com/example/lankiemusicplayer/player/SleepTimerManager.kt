package com.example.lankiemusicplayer.player

import androidx.compose.runtime.mutableStateOf
import androidx.media3.session.MediaController
import kotlinx.coroutines.*

object SleepTimerManager {

    private var timerJob: Job? = null

    val sleepTimerText = mutableStateOf<String?>(null)
    val isSleepTimerActive = mutableStateOf(false)
    val songsRemaining = mutableStateOf<Int?>(null)
    val isSongsMode = mutableStateOf(false)

    fun startMinutesTimer(
        minutes: Int,
        controller: MediaController?
    ) {

        timerJob?.cancel()

        isSleepTimerActive.value = true

        timerJob = CoroutineScope(Dispatchers.Main).launch {

            var remainingSeconds = minutes * 60

            while (remainingSeconds > 0) {

                val mins = remainingSeconds / 60
                val secs = remainingSeconds % 60

                sleepTimerText.value =
                    "🌙 Sleep Timer • ${mins}m ${secs}s left"

                delay(1000)

                remainingSeconds--
            }

            // fade out
            fadeOut(controller)

            controller?.pause()

            controller?.stop()

            sleepTimerText.value = null

            isSleepTimerActive.value = false
        }
    }

    fun cancelTimer() {

        timerJob?.cancel()

        sleepTimerText.value = null

        isSleepTimerActive.value = false

        songsRemaining.value = null

        isSongsMode.value = false
    }

    fun onSongCompleted(
        controller: MediaController?
    ) {

        if (!isSongsMode.value) return

        val remaining = songsRemaining.value ?: return

        val updated = remaining - 1

        songsRemaining.value = updated

        if (updated <= 0) {

            CoroutineScope(Dispatchers.Main).launch {

                fadeOut(controller)

                controller?.pause()

                controller?.stop()

                cancelTimer()
            }
        }

        else {

            sleepTimerText.value =
                "🌙 $updated song${if (updated > 1) "s" else ""} left"
        }
    }

    fun startSongsTimer(
        songs: Int
    ) {

        timerJob?.cancel()

        isSongsMode.value = true

        songsRemaining.value = songs

        isSleepTimerActive.value = true

        sleepTimerText.value =
            "🌙 $songs song${if (songs > 1) "s" else ""} left"
    }

    private suspend fun fadeOut(
        controller: MediaController?
    ) {

        controller ?: return

        val originalVolume = controller.volume

        for (i in 10 downTo 1) {

            controller.volume = originalVolume * (i / 10f)

            delay(300)
        }

        controller.volume = originalVolume
    }
}