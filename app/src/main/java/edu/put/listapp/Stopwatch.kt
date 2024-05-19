package edu.put.listapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.put.listapp.viewmodel.TrackViewModel

@Composable
fun Stopwatch(trackViewModel: TrackViewModel) {

    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                var showSaveDialog by remember {
                    mutableStateOf(false)
                }
                var showDeleteDialog by remember {
                    mutableStateOf(false)
                }
                if (showSaveDialog) {
                    trackViewModel.stopStopwatch()
                    AlertDialog(
                        onDismissRequest = {
                            showSaveDialog = false
                        },
                        title = {
                            Text(text = "Save result")
                        },
                        text = {
                            Text("Do you want to save your result?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    trackViewModel.resetStopwatch(save = true)
                                    showSaveDialog = false
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    trackViewModel.resetStopwatch(save = false)
                                    showSaveDialog = false
                                }
                            ) {
                                Text("Don't save")
                            }
                        }
                    )
                }
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },
                        title = {
                            Text(text = "Delete records")
                        },
                        text = {
                            Text("Do you want to delete all the records for this track?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    trackViewModel.deleteCurrentTrackRecords()
                                    trackViewModel.resetStopwatch(save = false)
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Don't delete")
                            }
                        }
                    )
                }
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
                                if (!trackViewModel.isRunning) {
                                    trackViewModel.startStopwatch()
                                } else {
                                    trackViewModel.stopStopwatch()
                                }
                            },
                            modifier = Modifier
                                .size(50.dp),
                            shape = CircleShape,
                            border = BorderStroke(2.dp, Color.Black),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black // Color of the content (icon)
                            )
                        ) {
                            if (trackViewModel.isRunning) {
                                Icon(
                                    Icons.Filled.Pause,
                                    contentDescription = "Play",
                                    modifier = Modifier.padding(2.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    modifier = Modifier.padding(2.dp)
                                )
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
                                if ((trackViewModel.isRunning)) {
                                    showSaveDialog = true
                                }
                            },
                            modifier = Modifier
                                .size(50.dp),
                            shape = CircleShape,
                            border = BorderStroke(2.dp, Color.Black),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black // Color of the content (icon)
                            )
                        ) {
                            Icon(
                                Icons.Filled.Stop,
                                contentDescription = "Stop",
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showDeleteDialog = true
                            },
                            modifier = Modifier
                                .size(50.dp),
                            shape = CircleShape,
                            border = BorderStroke(2.dp, Color.Black),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black // Color of the content (icon)
                            )
                        ) {
                            Icon(
                                Icons.Filled.DeleteForever,
                                contentDescription = "Stop",
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    Text(
                        text = trackViewModel.formatTime(),
                        modifier = Modifier.padding(8.dp), // Adjust padding as needed
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        lineHeight = 40.sp
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                visible = trackViewModel.stopwatchTrack.value != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                trackViewModel.stopwatchTrack.value?.let { track ->
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = "Currently running for",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = track.track.name,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    }
}