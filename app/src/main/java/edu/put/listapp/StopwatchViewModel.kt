package edu.put.listapp

import androidx.lifecycle.ViewModel

class StopwatchViewModel : ViewModel() {
    private var elapsedTimeMap = HashMap<String, Long>()

    fun updateTime(trackName: String, newElapsed: Long) {
        elapsedTimeMap[trackName] = newElapsed
    }

    fun getElapsedTime(trackName: String): Long {
        val currentValue = elapsedTimeMap.getOrDefault(trackName, 0L)
        if (!elapsedTimeMap.containsKey(trackName)) {
            elapsedTimeMap[trackName] = currentValue
        }
        return currentValue
    }
}