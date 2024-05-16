package edu.put.listapp

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.put.listapp.model.Loop
import edu.put.listapp.model.Track

@Composable
fun TrackDescription(
    track: Track,
    scrollState: ScrollState,
    headerHeight: Dp,
    showStopwatch: Boolean,
    trackViewModel: TrackViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(headerHeight))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            if (showStopwatch) {
                Stopwatch(trackViewModel)
                TrackRecords()
            } else {
                Text(
                    text = "Description:",
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 20.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = track.desc,
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
                track.loops.values.toList().forEach {
                    ListItem(loop = it, index = index++)
                }
            }
        }
    }
}

@Composable
fun TrackRecords() {
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
        Text(text = "2019-02-01 00:00:01")
        Text(text = "2019-02-01 00:00:02")
        Text(text = "2019-02-01 00:00:03")
    }
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

