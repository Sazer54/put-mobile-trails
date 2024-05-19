package edu.put.listapp.details

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import edu.put.listapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DetailsButtons(
    scroll: ScrollState, drawerState: DrawerState?, scope: CoroutineScope,
    headerHeightPx: Float, toolbarHeightPx: Float, showStopwatch: MutableState<Boolean>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    uri: Uri,
    context: Context
) {
    val collapseRange: Float = (headerHeightPx - toolbarHeightPx)
    val collapseFraction: Float = (scroll.value / collapseRange).coerceIn(0f, 1f)
    AnimatedVisibility(
        visible = collapseFraction < 1f,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            if (drawerState != null) {
                FloatingActionButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                }
            }
            DetailsActionButtons(
                modifier = Modifier.align(Alignment.TopEnd),
                showStopwatch = showStopwatch,
                cameraLauncher = cameraLauncher,
                uri = uri,
                context = context
            )
        }
    }
}

@Composable
fun DetailsActionButtons(
    modifier: Modifier,
    showStopwatch: MutableState<Boolean>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    uri: Uri,
    context: Context
) {
    val containerColor =
        if (showStopwatch.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val contentColor =
        if (showStopwatch.value) Color.White else MaterialTheme.colorScheme.onBackground
    Row(modifier = modifier) {
        FloatingActionButton(
            onClick = { showStopwatch.value = !showStopwatch.value },
            containerColor = containerColor,
            contentColor = contentColor
        ) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = "Show stopwatch"
            )
        }
        CameraButton(cameraLauncher, uri, context)
    }
}

@Composable
fun CameraButton(
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    uri: Uri,
    context: Context
) {


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    FloatingActionButton(
        modifier = Modifier.padding(start = 10.dp),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        onClick = {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(uri)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.photo_camera_svgrepo_com),
            contentDescription = "Open Camera",
            modifier = Modifier.size(24.dp)
        )
    }
}