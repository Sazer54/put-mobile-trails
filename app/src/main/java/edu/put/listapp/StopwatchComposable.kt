package edu.put.listapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchState(private val trackName: String, viewModel: StopwatchViewModel) {
    private val viewModel = viewModel
    private var job: Job? = null
    var isRunning by mutableStateOf(false)
    var elapsedMiliseconds by mutableLongStateOf(0L)

    fun startStopwatch() {
        isRunning = true
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isRunning) {
                delay(1000L)
                viewModel.updateTime(trackName, elapsedMiliseconds + 1000)
                elapsedMiliseconds = viewModel.getElapsedTime(trackName)
            }
        }
    }

    fun stopStopwatch() {
        isRunning = false
        job?.cancel()
    }

    fun resetStopwatch() {
        viewModel.updateTime(trackName, 0L)
        this.elapsedMiliseconds = 0L
        job?.cancel()
        isRunning = false
    }

    fun formatTime(): String {
        val seconds = elapsedMiliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val remainingMilliseconds = elapsedMiliseconds % 1000
        return String.format("%02d:%02d.%03d", minutes, remainingSeconds, remainingMilliseconds)
    }
}

@Composable
fun Stopwatch(stopwatchState: StopwatchState) {
    Box(
        modifier = Modifier.padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    OutlinedButton(
                        onClick = {
                            if(!stopwatchState.isRunning) {
                                stopwatchState.startStopwatch()
                            } else {
                                stopwatchState.stopStopwatch()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, Color.Black),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor =  Color.Black // Color of the content (icon)
                        )
                    ) {
                        if (stopwatchState.isRunning) {
                            Icon(Icons.Filled.Pause, contentDescription = "Play", modifier = Modifier.padding(2.dp))
                        } else {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play", modifier = Modifier.padding(2.dp))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    OutlinedButton(
                        onClick = {
                            stopwatchState.resetStopwatch()
                        },
                        modifier = Modifier
                            .size(50.dp),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, Color.Black),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor =  Color.Black // Color of the content (icon)
                        )
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = "Play", modifier = Modifier.padding(2.dp))
                    }
                }

                Text(
                    text = stopwatchState.formatTime(),
                    modifier = Modifier.padding(8.dp), // Adjust padding as needed
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    lineHeight = 40.sp
                )
            }
        }
    }

}