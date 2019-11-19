package com.jonnydev.bmp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.App
import com.jonnydev.bmp.util.TrackList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(private val mPlaylistId: Long) : AsyncViewModel() {
    private val mTrackDao = App.db.trackDao()
    private val mPlaylistDao = App.db.playlistDao()
    
    val trackList = MutableLiveData<TrackList>().apply {
        launch {
            val trackList = TrackList(mTrackDao.getAllByPlaylistId(mPlaylistId))
            
            postValue(trackList)
        }
    }
    
    suspend fun getPlaylistTitle(playlistId: Long) =
        withContext(Dispatchers.IO) {
            mPlaylistDao.getTitleById(playlistId)
        }
    
    fun deleteTrack(track: Track, tracksForUpdate: List<Track>) {
        launch {
            val tracks = mTrackDao.deleteUpdateAndGetAll(track, mPlaylistId, tracksForUpdate)
            updateTrackList(tracks)
        }
    }
    
    fun updatePositionsAfterDragging(tracksForUpdate: List<Track>) {
        launch {
            val tracks = mTrackDao.updatePositionsAndGetAll(mPlaylistId, tracksForUpdate)
            updateTrackList(tracks)
        }
    }
    
    private fun updateTrackList(tracks: MutableList<Track>) {
        val currentIndex = trackList.value?.currentIndex
        
        trackList.postValue(TrackList(tracks).apply {
            currentIndex?.let { changeSelectedPosition(it) }
        })
    }
}
