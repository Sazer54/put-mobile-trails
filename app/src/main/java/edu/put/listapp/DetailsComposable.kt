package edu.put.listapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.put.listapp.model.Loop
import edu.put.listapp.model.Track

@Composable
fun Details(track: Track) {
    val stopwatchViewModel: StopwatchViewModel = viewModel()
    val stopwatchState = remember { StopwatchState(track.name, stopwatchViewModel) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Box {
                AsyncImage(
                    model = track.largeImgURL,
                    contentDescription = "XD",
                    modifier = Modifier.fillMaxWidth()
                )
                CameraButton()
            }

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

@Composable
fun CameraButton() {
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                // Permission already granted, launch camera
                launchCamera(context, launcher)
            } else {
                // Request camera permission
                launcher.launch(
                    Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE
                    )
                )
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Camera,
            contentDescription = "Open Camera"
        )
    }
}
private fun launchCamera(context: android.content.Context, launcher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    launcher.launch(intent)
}