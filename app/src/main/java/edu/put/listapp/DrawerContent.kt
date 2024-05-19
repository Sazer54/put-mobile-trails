package edu.put.listapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawerContent(
    pagerState: PagerState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isTablet: Boolean
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
                modifier = Modifier.padding(bottom = 10.dp),
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
                modifier = Modifier.padding(bottom = 10.dp),
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
                modifier = Modifier.padding(bottom = 10.dp),
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
            if (!isTablet) {
                NavigationDrawerItem(
                    modifier = Modifier.padding(bottom = 10.dp),
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
}