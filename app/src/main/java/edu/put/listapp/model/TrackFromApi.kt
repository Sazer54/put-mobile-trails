package edu.put.listapp.model

data class TrackFromApi(
    val name: String,
    var desc: String,
    val address: String,
    val loops: Map<String, LoopFromApi>,
    val thumbURL: String,
    val largeImgURL: String,
    val difficulty: Int,
)

