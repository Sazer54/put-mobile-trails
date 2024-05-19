package edu.put.listapp.details

import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.put.listapp.Stopwatch
import edu.put.listapp.viewmodel.TrackViewModel
import edu.put.listapp.database.Loop
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun TrackDescription(
    scrollState: ScrollState,
    headerHeight: Dp,
    showStopwatch: MutableState<Boolean>,
    trackViewModel: TrackViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(headerHeight))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(LocalConfiguration.current.screenHeightDp.dp)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            if (showStopwatch.value) {
                Stopwatch(trackViewModel)
                TrackRecords(trackViewModel)
            } else {
                if (trackViewModel.selectedTrack.value!!.images.isNotEmpty()) {
                    Text(
                        text = "Gallery:",
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    ImageGridScreen(uriList = trackViewModel.selectedTrack.value!!.images.map { Uri.parse(it.uri) })
                }
                Text(
                    text = "Description:",
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 20.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = trackViewModel.selectedTrack.value!!.track.desc,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )
                Text(
                    text = "Routes:",
                    modifier = Modifier
                        .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                var index = 1
                trackViewModel.selectedTrack.value?.loops?.toList()?.forEach {
                    ListItem(loop = it, index = index++)
                }
            }
        }
    }
}

@Composable
fun TrackRecords(trackViewModel: TrackViewModel) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Your track records:",
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        val records = trackViewModel.selectedTrack.value!!.records
        records.forEach {
            val timestamp = getDateTimeComponents(it.timestamp)
            val time = getDateTimeComponents(it.time)

            // Format time components to ensure double digits
            val formattedTime = String.format(
                "%02d:%02d:%02d",
                time["hour"] ?: 0,
                time["minute"] ?: 0,
                time["second"] ?: 0
            )

            // Format timestamp components to ensure double digits
            val formattedTimestamp = String.format(
                "%04d-%02d-%02d %02d:%02d:%02d",
                timestamp["year"] ?: 0,
                timestamp["month"] ?: 0,
                timestamp["day"] ?: 0,
                timestamp["hour"] ?: 0,
                timestamp["minute"] ?: 0,
                timestamp["second"] ?: 0
            )

            Text(
                text = "$formattedTime @ $formattedTimestamp"
            )
        }

    }
}

fun getDateTimeComponents(timestamp: Long): Map<String, Int> {
    // Convert timestamp to Instant
    val instant = Instant.ofEpochMilli(timestamp)

    // Convert Instant to LocalDateTime using the system default time zone
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    // Retrieve individual components
    val year = localDateTime.year
    val month = localDateTime.monthValue
    val day = localDateTime.dayOfMonth
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val second = localDateTime.second

    // Return the components as a map
    return mapOf(
        "year" to year,
        "month" to month,
        "day" to day,
        "hour" to hour,
        "minute" to minute,
        "second" to second
    )
}

@Composable
fun ListItem(loop: Loop, index: Int) {
    Column(
        modifier = Modifier.padding(start = 20.dp)
    ) {
        Text(
            text = "$index. ${loop.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "Distance: ${loop.distance} miles",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
        Text(
            text = "Steps: ${loop.steps}",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}

