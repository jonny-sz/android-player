package com.jonnydev.bmp.util

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jonnydev.bmp.dao.PlaylistDao
import com.jonnydev.bmp.dao.TrackDao
import com.jonnydev.bmp.model.Playlist
import com.jonnydev.bmp.model.Track

@Database(entities = [Playlist::class, Track::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
}
