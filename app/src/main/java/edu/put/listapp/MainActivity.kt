package edu.put.listapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import edu.put.listapp.model.Track
import edu.put.listapp.ui.theme.ListAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    private lateinit var tracksList: List<Track>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            tracksList = withContext(Dispatchers.IO) {
                loadTracks()
            }

            setContent {
                ListAppTheme {
                    MyApp(this@MainActivity, tracksList = tracksList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("XD", "ded")
    }
}

@Composable
fun MyApp(context: Context, tracksList: List<Track>) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        if (isTablet) {
            var selectedTrack: Track? by remember { mutableStateOf(null) }

            TabletLayout(tracksList = tracksList, selectedTrack = selectedTrack) {
                selectedTrack = it
            }
        } else {
            PhoneLayout(context, tracksList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = "Cock",
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneLayout(context: Context, tracksList: List<Track>) {
    var query = remember { mutableStateOf("") }
    var active = remember { mutableStateOf(false) }
    val filteredTracksList = tracksList.filter { track ->
        track.name.contains(query.value, ignoreCase = true)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar() },
            content = {padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    SearchBar(
                        query = query.value,
                        onQueryChange = { query.value = it },
                        onSearch = {
                            active.value = false
                        },
                        active = true,
                        onActiveChange = {
                            active.value = it
                        },
                        content = {
                            ListComponent(filteredTracksList) {
                                launchIntentTrackDetails(context, it)
                            }
                        },
                        placeholder = {
                            Text(
                                text = "Search tracks...",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color.Gray
                                ),
                                textAlign = TextAlign.Left
                            )
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun TabletLayout(tracksList: List<Track>, selectedTrack: Track?, onTrackSelected: (Track) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(0.3f) // Add a border with 1dp width and black color
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Header()
                    Spacer(modifier = Modifier.height(16.dp))
                    ListComponent(tracksList.toList()) {
                        onTrackSelected(it)
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color.Black) // Change color as needed
            )
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                if (selectedTrack != null) {
                    //Details(activity, selectedTrack)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No track selected",
                            fontSize = 24.sp, // Adjust the font size as needed
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


private suspend fun loadTracks(): List<Track> {
    val apiUrlStub = "https://prescriptiontrails.org/api/trail/?id="
    val client = OkHttpClient()
    val gson = Gson()

    val tracksList = mutableListOf<Track>()
    coroutineScope {
        val deferredList = mutableListOf<Deferred<Track?>>()

        for(i in 1..10) {
            val deferred = async {
                val url = apiUrlStub + i.toString()
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Log.d("XDD", "nto successful")
                }
                val json = response.body?.string()
                val track = gson.fromJson(json, Track::class.java)
                track.desc = URLDecoder.decode(track.desc, "UTF-8")
                track
            }
            deferredList.add(deferred)
        }

        deferredList.forEach { deferred ->
            val track = deferred.await()
            track?.let {
                tracksList.add(it)
                Log.d("XD", track.thumbURL)
            }
        }
    }
    return tracksList
}

private fun launchIntentTrackDetails(context: Context, track: Track) {
    val intent = Intent(context, TrackDetailsActivity::class.java)
    intent.putExtra("track", track)
    context.startActivity(intent)
}

@Composable
fun Header() {
    Text(
        text = Constants.HEADER_TEXT,
        modifier = Modifier.padding(20.dp),
        style = TextStyle(
            fontSize = 22.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        textAlign = TextAlign.Left
    )
}


