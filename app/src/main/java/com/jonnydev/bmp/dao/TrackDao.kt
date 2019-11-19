package com.jonnydev.bmp.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.model.TrackPath
import kotlinx.coroutines.*
import java.io.File

@Dao
abstract class TrackDao : BaseDao<Track> {
    
    @Query("SELECT * FROM track ORDER BY track_playlist_id")
    abstract fun getAll(): MutableList<Track>
    
    @Query("SELECT track_id, track_path FROM track")
    abstract fun getAllPaths(): List<TrackPath>
    
    @Query("SELECT count(*) FROM track WHERE track_playlist_id =:playlistId")
    abstract fun getCountByPlaylistId(playlistId: Long): Int
    
    @Query("SELECT * FROM track WHERE track_playlist_id = :playlistId ORDER BY track_position ASC")
    abstract fun getAllByPlaylistId(playlistId: Long): MutableList<Track>
    
    @Query("DELETE FROM track")
    abstract fun deleteAll()
    
    @Query("DELETE FROM track WHERE track_id IN (:ids)")
    abstract fun deleteByIds(ids: List<Long>)
    
    @Query("UPDATE track SET track_position = :position WHERE track_id = :id")
    abstract fun updateTrackPosition(id: Long, position: Int)
    
    @Query("UPDATE track SET track_path = :path WHERE track_id = :id")
    abstract fun updateTrackPath(id: Long, path: String)
    
    @Query("SELECT DISTINCT track_playlist_id FROM track")
    abstract fun getPlaylistIds(): List<Long>
    
    @Transaction
    open fun insertWithPosition(playlistId: Long, vararg tracks: Track) {
        var nextPosition = getCountByPlaylistId(playlistId)
        
        for (track in tracks) {
            insert(track.copy(id = 0L, playlistId = playlistId, playlistPosition = nextPosition))
            nextPosition += 1
        }
    }
    
    @Transaction
    open fun updatePositionsAndGetAll(
        playlistId: Long,
        tracksForUpdate: List<Track>
    ): MutableList<Track> {
        if (tracksForUpdate.isNotEmpty()) {
            for (track in tracksForUpdate) {
                updateTrackPosition(track.id, track.playlistPosition)
            }
        }
        
        return getAllByPlaylistId(playlistId)
    }
    
    @Transaction
    open fun deleteUpdateAndGetAll(
        swipedTrack: Track,
        playlistId: Long,
        tracksForUpdate: List<Track>
    ): MutableList<Track> {
        deleteAndUpdate(swipedTrack, tracksForUpdate)
        
        return getAllByPlaylistId(playlistId)
    }
    
    private fun deleteAndUpdate(
        trackToDelete: Track,
        tracksForUpdate: List<Track>
    ) {
        delete(trackToDelete)
        
        if (tracksForUpdate.isNotEmpty()) {
            for (track in tracksForUpdate) {
                updateTrackPosition(track.id, track.playlistPosition - 1)
            }
        }
    }
    
    @Transaction
    open fun deleteAllAndGetAll(playlistId: Long): MutableList<Track> {
        deleteAll()
        return getAllByPlaylistId(playlistId)
    }
    
    @Transaction
    open fun updateAllTracks(scope: CoroutineScope) = scope.launch {
        val playlists = mutableListOf<Deferred<MutableList<Track>>>()
        val playlistIds = getPlaylistIds()
        val jobs = mutableListOf<Job>()
        
        for (id in playlistIds) {
            playlists.add(async { getAllByPlaylistId(id) })
        }
        for (deferredPlaylist in playlists) {
            jobs.add(
                launch {
                    val playlist = deferredPlaylist.await()
                    var position = 0
                    
                    for (track in playlist) {
                        if (!File(track.path).exists()) {
                            delete(track)
                        } else {
                            updateTrackPosition(track.id, position)
                            position += 1
                        }
                    }
                }
            )
        }
        
        jobs.forEach { it.join() }
    }
}
