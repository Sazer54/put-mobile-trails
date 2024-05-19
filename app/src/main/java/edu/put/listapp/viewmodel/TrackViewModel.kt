package edu.put.listapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import edu.put.listapp.database.AppDatabase
import edu.put.listapp.database.Record
import edu.put.listapp.database.Track
import edu.put.listapp.database.TrackDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackViewModel : ViewModel() {

    var tracksList: List<Track>? = null
    var selectedTrack: MutableState<TrackDetails?> = mutableStateOf(null)
    var stopwatchTrack: MutableState<TrackDetails?> = mutableStateOf(null)
    var job: Job? = null
    lateinit var db: AppDatabase

    var isRunning by mutableStateOf(false)
    var elapsedMiliseconds by mutableLongStateOf(0L)

    @OptIn(ObsoleteCoroutinesApi::class)
    fun startStopwatch() {
        stopwatchTrack.value = selectedTrack.value
        isRunning = true
        job = CoroutineScope(Dispatchers.Main).launch {
            val tickerChannel = ticker(1000L)
            for (event in tickerChannel) {
                if (!isRunning) {
                    tickerChannel.cancel()
                    break
                }
                elapsedMiliseconds+=1000
            }
        }
    }

    fun stopStopwatch() {
        isRunning = false
        job?.cancel()
    }

    fun resetStopwatch(save: Boolean) {
        if (isRunning) {
            stopStopwatch()
        }
        if (elapsedMiliseconds != 0L) {
            val record = Record(
                time = elapsedMiliseconds,
                trackId = stopwatchTrack.value!!.track.id,
                timestamp = System.currentTimeMillis()
            )
            CoroutineScope(Dispatchers.IO).launch {
                if (save) {
                    db.trackDao().insertRecord(record)
                    if (selectedTrack.value == stopwatchTrack.value) {
                        val updatedTrackDetails = db.trackDao().getTrackDetailsById(record.trackId)
                        withContext(Dispatchers.Main) {
                            selectedTrack.value = updatedTrackDetails
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    stopwatchTrack.value = null
                    elapsedMiliseconds = 0L
                }
            }
        } else {
            stopwatchTrack.value = null
            elapsedMiliseconds = 0L
        }
    }

    fun formatTime(): String {
        val seconds = elapsedMiliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun deleteCurrentTrackRecords() {
        CoroutineScope(Dispatchers.IO).launch {
            db.trackDao().deleteRecordsByTrackId(selectedTrack.value!!.track.id)
            val updatedTrackDetails = db.trackDao().getTrackDetailsById(selectedTrack.value!!.track.id)
            withContext(Dispatchers.Main) {
                selectedTrack.value = updatedTrackDetails
            }
        }
    }
}