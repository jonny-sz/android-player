package com.jonnydev.bmp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jonnydev.bmp.extension.toast
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.App
import com.jonnydev.bmp.util.AudioUtils
import com.jonnydev.bmp.util.TrackList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllTracksViewModel : AsyncViewModel() {
    val trackList = MutableLiveData<TrackList>().apply {
        launch {
            val tracks = AudioUtils.fetchTracks()
            
            postValue(TrackList(tracks))
        }
    }
    var selectedTracks: MutableList<Track> = mutableListOf()
    
    var isMultiSelectionMode: Boolean
        get() = trackList.value?.isMultiSelectionMode ?: false
        set(value) {
            trackList.value?.isMultiSelectionMode = value
        }
    
    fun addSelectedTracksToPlaylist(playListId: Long, playlistTitle: String) {
        val tracks = selectedTracks.toTypedArray()
        
        GlobalScope.launch(Dispatchers.IO) {
            App.db.trackDao().insertWithPosition(playListId, *tracks)
            withContext(Dispatchers.Main) { App.context.toast("${tracks.size} track(s) added to $playlistTitle") }
        }
    }
    
    fun uncheckSelectedTracks() {
        isMultiSelectionMode = false
        selectedTracks.forEach { it.isMultiSelected = false }
        selectedTracks.clear()
    }
    
    fun addSelectedTrack(track: Track) = selectedTracks.add(track)
    
    fun removeSelectedTrack(track: Track) = selectedTracks.remove(track)
}
