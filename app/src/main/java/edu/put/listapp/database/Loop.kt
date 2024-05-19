package edu.put.listapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.put.listapp.model.LoopFromApi

@Entity(
    tableName = "loop",
    foreignKeys = [ForeignKey(
        entity = Track::class,
        parentColumns = ["id"],
        childColumns = ["trackId"],
        onDelete = ForeignKey.CASCADE // Optional: Specify action on delete
    )],
    indices = [Index(value = ["trackId"])]
)
data class Loop(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name: String,
    val distance: String,
    val steps: String,
    val trackId: Long,
) {
    companion object {
        fun from(loopFromApi: LoopFromApi, trackId: Long): Loop {
            return Loop(
                name = loopFromApi.name,
                distance = loopFromApi.distance,
                steps = loopFromApi.steps,
                trackId = trackId
            )
        }
    }
}
