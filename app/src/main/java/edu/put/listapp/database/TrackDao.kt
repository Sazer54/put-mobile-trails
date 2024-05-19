package edu.put.listapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TrackDao {
    @Insert
    fun insertTrack(track: Track): Long

    @Insert
    fun insertRecord(record: Record)

    @Insert
    fun insertLoop(loop: Loop)

    @Insert
    fun insertImage(image: Image)

    @Query("SELECT * FROM track")
    fun getAllTracks(): List<Track>

    @Query("SELECT COUNT(*) from track")
    fun count(): Int

    @Transaction
    @Query("SELECT * FROM track LEFT JOIN record ON track.id = record.trackId")
    fun getTracksWithDetails(): List<TrackDetails>

    @Query("SELECT * FROM track WHERE id = :trackId")
    fun getTrackDetailsById(trackId: Long): TrackDetails

    @Query("DELETE FROM record WHERE trackId = :trackId")
    fun deleteRecordsByTrackId(trackId: Long)

}