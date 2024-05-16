package edu.put.listapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import edu.put.listapp.database.AppDatabase
import edu.put.listapp.database.Record
import edu.put.listapp.database.Track
import edu.put.listapp.database.TrackDao
import edu.put.listapp.database.TrackDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackViewModel : ViewModel() {
    var trackDetailsList: List<TrackDetails>? = null;
    var selectedTrack: MutableState<TrackDetails?> = mutableStateOf(null)
    var stopwatchTrack: MutableState<Track?> = mutableStateOf(null)
    var job: Job? = null
    lateinit var db: AppDatabase;

    var isRunning by mutableStateOf(false)
    var elapsedMiliseconds by mutableLongStateOf(0L)

    fun startStopwatch() {
        stopwatchTrack.value = selectedTrack.value!!.track
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
        val record = Record(
            time = elapsedMiliseconds,
            trackId = selectedTrack.value!!.track.id,
            timestamp = System.currentTimeMillis()
        )

        stopwatchTrack.value = null
        this.elapsedMiliseconds = 0L
        job?.cancel()
        isRunning = false

        // Insert the new record into the database and re-fetch the TrackDetails
        CoroutineScope(Dispatchers.IO).launch {
            db.trackDao().insertRecord(record)
            val updatedTrackDetails = db.trackDao().getTrackDetailsById(record.trackId)
            withContext(Dispatchers.Main) {
                selectedTrack.value = updatedTrackDetails
            }
        }
    }

    fun formatTime(): String {
        val seconds = elapsedMiliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val remainingMilliseconds = elapsedMiliseconds % 1000
        return String.format("%02d:%02d.%03d", minutes, remainingSeconds, remainingMilliseconds)
    }
}