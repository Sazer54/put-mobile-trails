package edu.put.listapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.put.listapp.database.Track

@Composable
fun ListComponent(tracksList: List<Track>, fontSize: Int, readabilityMode: MutableState<Boolean>, onItemClick: (Track) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        items(tracksList) {
            ListItem(
                name = it.name,
                thumbUrl = it.thumbURL,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable { onItemClick(it) },
                fontSize = fontSize,
                readabilityMode = readabilityMode
            )
        }
    }
}

@Composable
fun ListItem(name: String, thumbUrl: String, modifier: Modifier, fontSize: Int, readabilityMode: MutableState<Boolean>) {
    Card(
        modifier = modifier.shadow(elevation = 10.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
    ) {
        val gradientAlpha by animateFloatAsState(if (readabilityMode.value) 0.5f else 0f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RectangleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = thumbUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(
                        scale = 1f,
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RectangleShape)
                    .background(
                        brush = if (readabilityMode.value) {
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = gradientAlpha), Color.Transparent),
                                startY = 0.0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Transparent),
                                startY = 0.0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        }
                    ),
            )
            Text(
                text = name,
                style = TextStyle(
                    fontSize = fontSize.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .offset(
                        x = 2.dp,
                        y = 2.dp
                    )
                    .alpha(0.75f)
            )
            Text(
                text = name,
                style = TextStyle(
                    fontSize = fontSize.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}