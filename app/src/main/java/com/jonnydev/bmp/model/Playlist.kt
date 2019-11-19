package com.jonnydev.bmp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class Playlist(
    @ColumnInfo(name = "playlist_title") var title: String
) {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id") var id: Long = 0L
    
    override fun toString() = "Playlist(id=$id, title=$title)"
}
