package edu.put.listapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Track::class, Record::class, Loop::class, Image::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

}