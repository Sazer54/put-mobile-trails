package edu.put.listapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.put.listapp.model.Loop

@Entity(tableName = "track")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    var desc: String,
    val address: String,
    //val loops: Map<String, Loop>,
    val thumbURL: String,
    val largeImgURL: String,
    val difficulty: Int,
)
