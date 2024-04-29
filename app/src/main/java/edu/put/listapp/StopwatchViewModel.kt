package edu.put.listapp

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class StopwatchViewModel : ViewModel() {
    val elapsedMilliseconds = mutableLongStateOf(0L)

    fun updateElapsedMilliseconds(newElapsed: Long) {
        elapsedMilliseconds.longValue = newElapsed
    }
}