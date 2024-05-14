package edu.put.listapp.model

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val name: String,
    var desc: String,
    val address: String,
    val loops: Map<String, Loop>,
    val thumbURL: String,
    val largeImgURL: String,
    val secondsElapsed: Int = 0,
    val difficulty: Int,
)

