package edu.put.listapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.put.listapp.model.Track

@Composable
fun ListComponent(tracksList: List<Track>, onItemClick: (Track) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(count = 2), modifier = Modifier.padding(horizontal = 8.dp)) {
        items(tracksList) {
            ListItem(
                name = it.name,
                address = it.address,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable { onItemClick(it) }
            )
        }
    }
}

@Composable
fun ListItem(name: String, address: String, modifier: Modifier) {
    Card (
        modifier = modifier,
        shape = RoundedCornerShape(8.dp) // Adjust corner radius as needed
    ){
        Column(
            modifier = modifier
        ) {
            Text(
                text = name,
                modifier = Modifier.padding(top = 8.dp, start = 20.dp),
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                textAlign = TextAlign.Left
            )
            Text(
                text = address,
                modifier = Modifier.padding(bottom = 8.dp, start = 20.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                textAlign = TextAlign.Left
            )
        }
    }

}