package edu.put.listapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.put.listapp.model.TrackFromApi

@Entity(tableName = "track")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    var desc: String,
    val address: String,
    val thumbURL: String,
    val largeImgURL: String,
    val difficulty: Int,
) {
    companion object {
        fun from(trackFromApi: TrackFromApi): Track {
            return Track(
                name = trackFromApi.name,
                desc = trackFromApi.desc,
                address = trackFromApi.address,
                thumbURL = trackFromApi.thumbURL,
                largeImgURL = trackFromApi.largeImgURL,
                difficulty = trackFromApi.difficulty
            )
        }
    }
}
