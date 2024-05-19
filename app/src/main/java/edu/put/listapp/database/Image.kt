package edu.put.listapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image",
    foreignKeys = [ForeignKey(
        entity = Track::class,
        parentColumns = ["id"],
        childColumns = ["trackId"],
        onDelete = ForeignKey.CASCADE // Optional: Specify action on delete
    )],
    indices = [Index(value = ["trackId"])]
)
data class Image(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val trackId: Long,
    val uri: String,
)