package edu.put.listapp

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.put.listapp.model.Loop
import edu.put.listapp.model.Track

@Composable
fun Details(activity: ComponentActivity, track: Track) {
    val stopwatchViewModel: StopwatchViewModel = viewModel()
    val stopwatchState = remember { StopwatchState(stopwatchViewModel) }

     LaunchedEffect(activity) {
         activity.lifecycle.addObserver(LifecycleEventObserver { _, event ->
             if (event == Lifecycle.Event.ON_DESTROY) {
                 stopwatchViewModel.updateElapsedMilliseconds(stopwatchState.elapsedMiliseconds)
             }
         })
     }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Text(
                text = track.name,
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 40.sp
            )
            Stopwatch(stopwatchState)
            Text(
                text=track.desc,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            )
            Text(
                text="Routes:",
                modifier = Modifier
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        var index = 1
        items(track.loops.values.toList()) {
            ListItem(loop = it, index = index++)
        }
    }
}

@Composable
private fun ListItem(loop: Loop, index: Int) {
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
            text="Distance: ${loop.distance} miles",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
        Text(
            text="Steps: ${loop.steps}",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}