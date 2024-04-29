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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
}

@Composable
fun MyApp(context: Context, tracksList: List<Track>) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet) {
        var selectedTrack: Track? by remember { mutableStateOf(null) }

        TabletLayout(tracksList = tracksList, selectedTrack = selectedTrack) {
            selectedTrack = it
        }
    } else {
        PhoneLayout(context, tracksList)
    }
}

@Composable
fun PhoneLayout(context: Context, tracksList: List<Track>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Header()
            Spacer(modifier = Modifier.height(16.dp))
            ListComponent(tracksList.toList()) {
                launchIntentTrackDetails(context, it)
            }
        }
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


