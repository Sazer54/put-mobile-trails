package edu.put.listapp

sealed class Screen(val route: String) {
    object HomePage: Screen("homePage")
    object TrackListEasy : Screen("trackListEasy")
    object TrackListHard: Screen("trackListHard")
    object TrackDetails : Screen("trackDetails/{trackName}") {
        fun createRoute(trackName: String) = "trackDetails/$trackName"
    }
}