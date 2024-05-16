package edu.put.listapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "record",
    foreignKeys = [ForeignKey(
        entity = Track::class,
        parentColumns = ["id"],
        childColumns = ["trackId"],
        onDelete = ForeignKey.CASCADE // Optional: Specify action on delete
    )],
    indices = [Index(value = ["trackId"])]
)
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long, // Example field for the recorded time
    val trackId: Int, // Foreign key reference to Track
    val timestamp: Long,
)

