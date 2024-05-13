package edu.put.listapp

sealed class Screen(val route: String) {
    object TrackList : Screen("trackList")
    object TrackDetails : Screen("trackDetails/{trackName}") {
        fun createRoute(trackName: String) = "trackDetails/$trackName"
    }
}