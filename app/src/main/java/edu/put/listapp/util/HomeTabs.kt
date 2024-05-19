package edu.put.listapp.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeTabs(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String
) {
    Home(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        text = "Home"
    ),
    EasyTracks(
        selectedIcon = Icons.Filled.Hiking,
        unselectedIcon = Icons.Outlined.Hiking,
        text = "Easy Hike"
    ),
    HardTracks(
        selectedIcon = Icons.AutoMirrored.Filled.DirectionsRun,
        unselectedIcon = Icons.AutoMirrored.Outlined.DirectionsRun,
        text = "Hard Hike"
    ),
    Details(
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star,
        text = "Your track"
    )
}