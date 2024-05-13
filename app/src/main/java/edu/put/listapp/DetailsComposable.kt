package edu.put.listapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.put.listapp.model.Loop
import edu.put.listapp.model.Track
import kotlinx.coroutines.launch

@Composable
fun Details(track: Track, listState: LazyListState) {
    val stopwatchViewModel: StopwatchViewModel = viewModel()
    val stopwatchState = remember { StopwatchState(track.name, stopwatchViewModel) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            ExpandedTopBar(track)
        }
        item {
            Stopwatch(stopwatchState)
            Text(
                text=track.desc,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
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
private fun ExpandedTopBar(track: Track) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = track.largeImgURL,
                contentDescription = "XD",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            CameraButton()
            Text(
                modifier = Modifier.padding(16.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 2.dp, y = 2.dp)
                    .alpha(0.75f),
                text = track.name,
                color = Color.DarkGray,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                modifier = Modifier.padding(16.dp)
                    .align(Alignment.BottomStart),
                text = track.name,
                color = MaterialTheme.colorScheme.onPrimary,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            FloatingActionButton(
                onClick = { (context as Activity).finish() }, // Finish the current activity when clicked
                modifier = Modifier
                    .align(Alignment.TopStart) // Align to the top start
                    .padding(16.dp), // Add some padding
                content = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, // Use the ArrowBack icon
                        contentDescription = "Go back"
                    )
                }
            )

        }

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
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera(context)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    launcher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Open Camera"
            )
        }
    }
}

fun openCamera(context: Context) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    context.startActivity(intent)
}