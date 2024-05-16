package edu.put.listapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import edu.put.listapp.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackViewModel : ViewModel() {
    var selectedTrack: Track? = null
    var stopwatchTrack: MutableState<Track?> = mutableStateOf(null)
    var job: Job? = null

    var isRunning by mutableStateOf(false)
    var elapsedMiliseconds by mutableLongStateOf(0L)

    fun startStopwatch() {
        stopwatchTrack.value = selectedTrack
        isRunning = true
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isRunning) {
                delay(1000L)
                elapsedMiliseconds += 1000
            }
        }
    }

    fun stopStopwatch() {
        isRunning = false
        job?.cancel()
    }

    fun resetStopwatch() {
        stopwatchTrack.value = null
        this.elapsedMiliseconds = 0L
        job?.cancel()
        isRunning = false
    }

    fun formatTime(): String {
        val seconds = elapsedMiliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val remainingMilliseconds = elapsedMiliseconds % 1000
        return String.format("%02d:%02d.%03d", minutes, remainingSeconds, remainingMilliseconds)
    }
}