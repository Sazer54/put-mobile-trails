package edu.put.listapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.gson.Gson
import edu.put.listapp.database.AppDatabase
import edu.put.listapp.database.Record
//import edu.put.listapp.model.Track
import edu.put.listapp.database.Track
import edu.put.listapp.database.TrackDetails
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
    private lateinit var tracksList: List<TrackDetails>

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
            Log.d("count", "$count")
            if (count == 0) {
                tracksList = withContext(Dispatchers.IO) {
                    loadTracks()
                }
                withContext(Dispatchers.IO) {
                    tracksList.forEach {
                        dao.insertTrack(it.track)
                        dao.insertRecord(Record(time = 1000, trackId = it.track.id, timestamp = System.currentTimeMillis()))
                    }
                }
            } else {
                tracksList = withContext(Dispatchers.IO) {
                    dao.getTracksWithDetails()
                }
            }
            Log.d("actual count", tracksList.size.toString())
            Log.d("record", tracksList[0].records[0].time.toString())

            setContent {
                ListAppTheme {
                    MyApp(tracksList = tracksList, db)
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
fun MyApp(tracksList: List<TrackDetails>, db: AppDatabase) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600



    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        if (isTablet) {
            var selectedTrack: Track? by remember { mutableStateOf(null) }

            /*TabletLayout(tracksList = tracksList, selectedTrack = selectedTrack) {
                selectedTrack = it
            }*/
        } else {
            AppNavigator(tracksList, db)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavigator(tracksList: List<TrackDetails>, db: AppDatabase) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val selectedTabIndex = remember {
        derivedStateOf { pagerState.currentPage }
    }
    val trackViewModel: TrackViewModel = viewModel()
    trackViewModel.trackDetailsList = tracksList
    trackViewModel.db = db

    val easyTracks = tracksList.filter { it.track.difficulty == 1 }
    val hardTracks = tracksList.filter { it.track.difficulty >= 2 }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                pagerState = pagerState,
                drawerState = drawerState,
                scope = scope,
                selectedTrack = trackViewModel.selectedTrack.value?.track
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
                                TrackDifficulty.EASY
                            )

                            2 -> PhoneLayout(
                                hardTracks,
                                drawerState,
                                scope,
                                pagerState,
                                trackViewModel,
                                TrackDifficulty.HARD
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
fun TopBar(title: String, drawerState: DrawerState, scope: CoroutineScope) {
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhoneLayout(
    tracksList: List<TrackDetails>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    pagerState: PagerState,
    trackViewModel: TrackViewModel,
    trackDifficulty: TrackDifficulty
) {
    var query = remember { mutableStateOf("") }
    var active = remember { mutableStateOf(false) }
    val filteredTracksList = tracksList.filter { track ->
        track.track.name.contains(query.value, ignoreCase = true)
    }
    val title = if (trackDifficulty == TrackDifficulty.EASY)
        "Easy tracks" else "Hard tracks"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(title, drawerState, scope) },
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
                        ListComponent(filteredTracksList) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val track = trackViewModel.db.trackDao().getTrackDetailsById(it.track.id)
                                withContext(Dispatchers.Main) {
                                    trackViewModel.selectedTrack.value = track
                                    scope.launch {
                                        pagerState.animateScrollToPage(3)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawerContent(
    pagerState: PagerState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    selectedTrack: Track?
) {
    ModalDrawerSheet {
        Box {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = "Trail tracker",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 2.dp, y = 2.dp)
                    .alpha(0.75f),
                color = Color.DarkGray,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Trail tracker",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                color = Color.White,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                },
                label = { Text(text = "Home") },
                selected = pagerState.currentPage == 0,
                onClick = {
                    if (pagerState.currentPage != 0) {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                    if (drawerState.isOpen) scope.launch { drawerState.close() }
                }
            )
            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Filled.Landscape, contentDescription = "Menu")
                },
                label = { Text(text = "Easy tracks list") },
                selected = pagerState.currentPage == 1,
                onClick = {
                    if (pagerState.currentPage != 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                    if (drawerState.isOpen) scope.launch { drawerState.close() }
                }
            )

            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Filled.Landscape, contentDescription = "Menu")
                },
                label = { Text(text = "Hard tracks list") },
                selected = pagerState.currentPage == 2,
                onClick = {
                    if (pagerState.currentPage != 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                    if (drawerState.isOpen) scope.launch { drawerState.close() }
                }
            )

            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Filled.Star, contentDescription = "Menu")
                },
                label = { Text(text = "Your track") },
                selected = pagerState.currentPage == 3,
                onClick = {
                    if (pagerState.currentPage != 3) {
                        scope.launch {
                            pagerState.animateScrollToPage(3)
                        }
                    }
                    if (drawerState.isOpen) scope.launch { drawerState.close() }
                }
            )
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
                    TabletHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                    /*ListComponent(tracksList.toList()) {
                        onTrackSelected(it)
                    }*/
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


private suspend fun loadTracks(): List<TrackDetails> {
    val apiUrlStub = "https://prescriptiontrails.org/api/trail/?id="
    val client = OkHttpClient()
    val gson = Gson()

    val tracksList = mutableListOf<Track>()
    coroutineScope {
        val deferredList = mutableListOf<Deferred<Track?>>()

        for (i in 1..10) {
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
    val tracksDetailsList = tracksList.map {
        TrackDetails(it, emptyList())
    }
    return tracksDetailsList
}

@Composable
fun TabletHeader() {
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


