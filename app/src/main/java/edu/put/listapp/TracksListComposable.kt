package edu.put.listapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Alignment.Companion.Bottom
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.put.listapp.model.Track

@Composable
fun ListComponent(tracksList: List<Track>, onItemClick: (Track) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        items(tracksList) {
            ListItem(
                name = it.name,
                address = it.address,
                thumbUrl = it.thumbURL,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable { onItemClick(it) }
            )
        }
    }
}

@Composable
fun ListItem(name: String, address: String, thumbUrl: String, modifier: Modifier) {
    Card (
        modifier = modifier.shadow(elevation = 10.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
    ){
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
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 26.sp,
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
                    fontSize = 26.sp,
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