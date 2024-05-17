package edu.put.listapp.database

import androidx.room.Embedded
import androidx.room.Relation

data class TrackDetails(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId"
    )
    val records: List<Record>
)
