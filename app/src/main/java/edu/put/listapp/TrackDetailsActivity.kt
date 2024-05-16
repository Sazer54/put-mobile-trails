package edu.put.listapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.put.listapp.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow


private val headerHeight = 275.dp

@Composable
fun TrackDetailsScreen(
    trackViewModel: TrackViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
) {
    val scrollState = rememberScrollState(0)
    Box {
        if (trackViewModel.selectedTrack != null) {
            AwesomeToolbar(
                trackViewModel.selectedTrack!!,
                scrollState,
                drawerState,
                scope,
                trackViewModel
            )
        } else {
            ChooseTrackInfo(drawerState, scope)
        }
    }
}

@Composable
fun ChooseTrackInfo(drawerState: DrawerState, scope: CoroutineScope) {
    Scaffold(
        topBar = { TopBar(title = "Track details", drawerState = drawerState, scope = scope) },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(20.dp),
                ) {
                    Icon(
                        Icons.Filled.Landscape,
                        contentDescription = "Menu",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        text = "Pick a track from the lists to see its details!",
                    )
                }
            }
        }
    )
}

@Composable
fun AwesomeToolbar(
    track: Track,
    scroll: ScrollState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    trackViewModel: TrackViewModel
) {
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val toolbarHeightPx = with(LocalDensity.current) { 64.dp.toPx() }
    val showStopwatch = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Header(scroll, track.largeImgURL, headerHeightPx)
        TrackDescription(track, scroll, headerHeight, showStopwatch.value, trackViewModel)
        Toolbar(scroll, headerHeightPx, toolbarHeightPx, drawerState, scope, showStopwatch, trackViewModel)
        Title(scroll, track.name, headerHeightPx, toolbarHeightPx)
        DetailsButtons(scroll, drawerState, scope, headerHeightPx, toolbarHeightPx, showStopwatch)
    }
}

@Composable
fun Header(scroll: ScrollState, imageUrl: String, headerHeightPx: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                alpha = (-1f / headerHeightPx) * scroll.value + 1
            },
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000)),
                        startY = 3 * headerHeightPx / 4
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    scroll: ScrollState,
    headerHeightPx: Float,
    toolbarHeightPx: Float,
    drawerState: DrawerState,
    scope: CoroutineScope,
    showStopwatch: MutableState<Boolean>,
    trackViewModel: TrackViewModel
) {
    val toolbarBottom = headerHeightPx - toolbarHeightPx
    val showToolbar by remember {
        derivedStateOf { scroll.value >= toolbarBottom }
    }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera(context)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    val titleFontSize by remember {
        derivedStateOf { if (trackViewModel.selectedTrack!!.name.length > 16) 18.sp else 26.sp }
    }

    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary // Replace with your desired color
            ),
            navigationIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        showStopwatch.value = !showStopwatch.value
                    },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            launcher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            title = {
                Text(
                    text = trackViewModel.selectedTrack!!.name,
                    modifier = Modifier
                        .padding(16.dp),
                    color = Color.White,
                    style = TextStyle(
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
        )
    }
}

private const val titleFontScaleStart = 1f
private const val titleFontScaleEnd = 0.66f

@Composable
fun Title(scroll: ScrollState, trackName: String, headerHeightPx: Float, toolbarHeightPx: Float) {
    var titleHeightPx by remember { mutableFloatStateOf(0f) }
    val paddingMedium = 10.dp
    val titlePaddingStart = paddingMedium
    val titlePaddingEnd = paddingMedium + 16.dp
    val titlePaddingStartPx = with(LocalDensity.current) { titlePaddingStart.toPx() }
    val titlePaddingEndPx = with(LocalDensity.current) { titlePaddingEnd.toPx() }
    val paddingPx = with(LocalDensity.current) { paddingMedium.toPx() }

    val collapseRange: Float = (headerHeightPx - toolbarHeightPx)
    val collapseFraction: Float = (scroll.value / collapseRange).coerceIn(0f, 1f)

    val scaleXY = lerp(
        titleFontScaleStart.dp,
        titleFontScaleEnd.dp,
        collapseFraction
    )

    AnimatedVisibility(
        visible = collapseFraction < 1f,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val titleY: Float = (1f - collapseFraction).pow(2) *
                            (headerHeightPx - titleHeightPx - paddingPx) +
                            2 * collapseFraction * (1 - collapseFraction) * headerHeightPx / 2 +
                            collapseFraction.pow(2) * (toolbarHeightPx / 2 - titleHeightPx / 2)

                    val titleX: Float =
                        (1f - collapseFraction).pow(2) * (titlePaddingStartPx) +
                                2 * collapseFraction * (1 - collapseFraction) * titlePaddingEndPx *
                                5 / 4 + collapseFraction.pow(2) * titlePaddingEndPx

                    translationY = titleY
                    translationX = titleX
                    scaleX = scaleXY.value
                    scaleY = scaleXY.value
                }
                .onGloballyPositioned {
                    titleHeightPx = it.size.height.toFloat()
                }
        ) {
            Text(
                text = trackName,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 2.dp, y = 2.dp)
                    .alpha(0.75f),
                color = Color.DarkGray,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = trackName,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                color = Color.White,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

}

@Composable
fun DetailsButtons(
    scroll: ScrollState, drawerState: DrawerState, scope: CoroutineScope,
    headerHeightPx: Float, toolbarHeightPx: Float, showStopwatch: MutableState<Boolean>
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera(context)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

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
            FloatingActionButton(
                modifier = Modifier.align(Alignment.TopStart),
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                },
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Open menu")
            }
            DetailsActionButtons(
                modifier = Modifier.align(Alignment.TopEnd),
                scope = scope,
                launcher = launcher,
                showStopwatch = showStopwatch
            )
        }
    }
}

@Composable
fun DetailsActionButtons(
    modifier: Modifier,
    scope: CoroutineScope,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    showStopwatch: MutableState<Boolean>
) {
    Row(modifier = modifier) {
        FloatingActionButton(onClick = { showStopwatch.value = !showStopwatch.value }) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = "Show stopwatch"
            )
        }
        FloatingActionButton(
            modifier = Modifier.padding(start = 10.dp),
            onClick = {
                scope.launch {
                    launcher.launch(Manifest.permission.CAMERA)
                }
            },
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

