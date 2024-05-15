package edu.put.listapp

import androidx.lifecycle.ViewModel
import edu.put.listapp.model.Track

class TrackViewModel : ViewModel() {
    var selectedTrack: Track? = null
}