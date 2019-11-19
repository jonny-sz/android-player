package com.jonnydev.bmp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.jonnydev.bmp.model.Playlist

@Dao
abstract class PlaylistDao : BaseDao<Playlist> {
    
    @Query("SELECT * FROM playlist ORDER BY playlist_title COLLATE NOCASE ASC")
    abstract fun getAll(): LiveData<MutableList<Playlist>>
    
    @Query("SELECT playlist_title FROM playlist WHERE playlist_id == :playlistId")
    abstract fun getTitleById(playlistId: Long): String
    
    @Query("DELETE FROM playlist")
    abstract fun deleteAll()
}
