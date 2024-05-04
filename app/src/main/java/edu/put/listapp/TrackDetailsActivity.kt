package edu.put.listapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import edu.put.listapp.model.Track
import edu.put.listapp.ui.theme.ListAppTheme

class TrackDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val track = intent.getParcelableExtra<Track>("track")
                    Details(track!!)
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("XD", "ded details")
    }
}


