package edu.put.listapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.put.listapp.model.Track
import kotlinx.coroutines.CoroutineScope

@Composable
fun HomePage(navController: NavController, drawerState: DrawerState, scope: CoroutineScope, selectedTrack: Track?) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(
            navController = navController,
            drawerState = drawerState,
            scope = scope,
            selectedTrack = selectedTrack
        )
        },
        content = {
            Scaffold(
                topBar = {
                    TopBar(title = "Trail Tracker", drawerState = drawerState, scope = scope)
                },
                content = { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        HomePageContent()
                    }
                }
            )
        }
    )
}

@Composable
fun HomePageContent() {
    val heightPx = with (LocalDensity.current) {LocalConfiguration.current.screenHeightDp.dp.toPx()}
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = heightPx / 16,
        endY = heightPx *7/8
    )
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.matchParentSize().background(gradient))
        ImageText()
    }
}

@Composable
fun ImageText() {
    Box(
        modifier = Modifier.fillMaxSize().padding(vertical = 50.dp, horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            WelcomeText()
            DescriptionText()
        }
    }
}

@Composable
fun WelcomeText() {
    Box(modifier = Modifier.padding(bottom = 70.dp)) {
        Text(
            text = "Welcome!",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp,
                color = Color.DarkGray
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .offset(x = 5.dp, y = 5.dp)
                .alpha(0.75f),
        )
        Text(
            text = "Welcome!",
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
        )
    }
}

@Composable
fun DescriptionText() {
    Box {
        Text(
            text = "This app is designed to help you keep track of your trail walks.\n" +
                    "You can browse a list of available trails, track your time, and view your progress.\n\n" +
                    "To get started, click the menu icon in the top left corner to view the trails we prepared for you.\n\n" +
                    "Select a trail to view more details and start tracking your time. Enjoy your run!",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.DarkGray
            ),
            modifier = Modifier
                .offset(x = 5.dp, y = 5.dp)
                .alpha(0.75f),
        )
        Text(
            text = "This app is designed to help you keep track of your trail walks.\n" +
                    "You can browse a list of available trails, track your time, and view your progress.\n\n" +
                    "To get started, click the menu icon in the top left corner to view the trails we prepared for you.\n\n" +
                    "Select a trail to view more details and start tracking your time. Enjoy your run!",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
        )
    }
}