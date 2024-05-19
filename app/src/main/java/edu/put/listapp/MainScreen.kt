package edu.put.listapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.google.gson.Gson
import edu.put.listapp.database.AppDatabase
import edu.put.listapp.database.Loop
import edu.put.listapp.database.Record
//import edu.put.listapp.model.Track
import edu.put.listapp.database.Track
import edu.put.listapp.details.ChooseTrackInfoContent
import edu.put.listapp.details.TrackDetailsLayout
import edu.put.listapp.details.TrackDetailsScreen
import edu.put.listapp.model.TrackFromApi
import edu.put.listapp.ui.theme.ListAppTheme
import edu.put.listapp.util.HomeTabs
import edu.put.listapp.util.TrackDifficulty
import edu.put.listapp.viewmodel.TrackViewModel
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
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        val dao = db.trackDao()

        CoroutineScope(Dispatchers.Main).launch {
            val count = withContext(Dispatchers.IO) {
                dao.count()
            }
            if (count == 0) {
                val tracksFromApiList = withContext(Dispatchers.IO) {
                    loadTracks()
                }
                withContext(Dispatchers.IO) {
                    tracksFromApiList.forEach {
                        val newTrackId = dao.insertTrack(Track.from(it))
                        it.loops.values.forEach { loop ->
                            dao.insertLoop(Loop.from(loop, newTrackId))
                        }
                        dao.insertRecord(Record(time = 1000, trackId = newTrackId, timestamp = System.currentTimeMillis()))
                    }
                }
            }
            tracksList = withContext(Dispatchers.IO) {
                dao.getAllTracks()
            }

            setContent {
                ListAppTheme {
                    MyApp(tracksList = tracksList, db)
                }
            }
        }
    }
}

@Composable
fun MyApp(tracksList: List<Track>, db: AppDatabase) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val trackViewModel: TrackViewModel = viewModel()
    trackViewModel.tracksList = tracksList
    trackViewModel.db = db

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        if (isTablet) {
            TabletLayout(trackViewModel, true)
        } else {
            AppNavigator(trackViewModel, false)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavigator(trackViewModel: TrackViewModel, isTablet: Boolean) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val selectedTabIndex = remember {
        derivedStateOf { pagerState.currentPage }
    }

    val easyTracks = trackViewModel.tracksList!!.filter { it.difficulty == 1 }
    val hardTracks = trackViewModel.tracksList!!.filter { it.difficulty >= 2 }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                pagerState = pagerState,
                drawerState = drawerState,
                scope = scope,
                isTablet = isTablet
            )
        },
        content = {
            Scaffold(
                bottomBar = {
                    TabRow(
                        selectedTabIndex = selectedTabIndex.value,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HomeTabs.entries.forEachIndexed() { index, tab ->
                            if (!(isTablet && index == 3)) {
                                Tab(
                                    selected = selectedTabIndex.value == index,
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.outline,
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(tab.ordinal)
                                        }
                                    },
                                    text = {Text(text = tab.text)},
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTabIndex.value == index)
                                                tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.text
                                        )
                                    }

                                )
                            }
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    HorizontalPager(state = pagerState) { page ->
                        when (page) {
                            0 -> HomePage(drawerState, scope)
                            1 -> PhoneLayout(
                                easyTracks,
                                drawerState,
                                scope,
                                pagerState,
                                trackViewModel,
                                TrackDifficulty.EASY,
                                isTablet
                            )

                            2 -> PhoneLayout(
                                hardTracks,
                                drawerState,
                                scope,
                                pagerState,
                                trackViewModel,
                                TrackDifficulty.HARD,
                                isTablet
                            )

                            3 -> TrackDetailsScreen(
                                trackViewModel,
                                drawerState,
                                scope
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, drawerState: DrawerState, scope: CoroutineScope, readabilityMode: MutableState<Boolean>?){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        ),
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        actions = {
            if (readabilityMode != null) {
                IconButton(onClick = {
                    readabilityMode.value = !readabilityMode.value
                }) {
                    Icon(
                        imageVector = Icons.Filled.RemoveRedEye,
                        contentDescription = "Readability",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhoneLayout(
    tracksList: List<Track>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    pagerState: PagerState,
    trackViewModel: TrackViewModel,
    trackDifficulty: TrackDifficulty,
    isTablet: Boolean
) {
    var query = remember { mutableStateOf("") }
    var active = remember { mutableStateOf(false) }
    val filteredTracksList = tracksList.filter { track ->
        track.name.contains(query.value, ignoreCase = true)
    }
    val title = if (trackDifficulty == TrackDifficulty.EASY)
        "Easy tracks" else "Hard tracks"

    val readabilityMode = remember { mutableStateOf(false) }
    val fontSize by animateIntAsState(
        targetValue = if (readabilityMode.value) 44 else 24
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(title, drawerState, scope, readabilityMode) },
        content = { padding ->
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
                        ListComponent(filteredTracksList, fontSize, readabilityMode) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val trackWithDetails = trackViewModel.db.trackDao().getTrackDetailsById(it.id)
                                withContext(Dispatchers.Main) {
                                    trackViewModel.selectedTrack.value = trackWithDetails
                                    if (!isTablet) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(3)
                                        }
                                    }
                                }
                            }
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

@Composable
fun TabletLayout(trackViewModel: TrackViewModel, isTablet: Boolean) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(0.3f) // Add a border with 1dp width and black color
            ) {
                AppNavigator(trackViewModel = trackViewModel, isTablet = isTablet)
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
                if (trackViewModel.selectedTrack.value != null) {
                    TrackDetailsLayout(null, rememberCoroutineScope(), trackViewModel)
                } else {
                    ChooseTrackInfoContent(padding = null)
                }
            }
        }
    }
}


private suspend fun loadTracks(): List<TrackFromApi> {
    val apiUrlStub = "https://prescriptiontrails.org/api/trail/?id="
    val client = OkHttpClient()
    val gson = Gson()

    val tracksList = mutableListOf<TrackFromApi>()
    coroutineScope {
        val deferredList = mutableListOf<Deferred<TrackFromApi?>>()

        for (i in 1..10) {
            val deferred = async {
                val url = apiUrlStub + i.toString()
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Log.d("Load", "error")
                }
                val json = response.body?.string()
                val track = gson.fromJson(json, TrackFromApi::class.java)
                track.desc = URLDecoder.decode(track.desc, "UTF-8")
                track
            }
            deferredList.add(deferred)
        }

        deferredList.forEach { deferred ->
            val track = deferred.await()
            track?.let {
                tracksList.add(it)
            }
        }
    }
    return tracksList
}


