package com.jonnydev.bmp.viewmodel

import com.jonnydev.bmp.extension.toast
import com.jonnydev.bmp.model.Playlist
import com.jonnydev.bmp.util.App
import kotlinx.coroutines.launch

class PlaylistChooserViewModel : AsyncViewModel() {
    private val mPlaylistDao = App.db.playlistDao()
    val playlists = mPlaylistDao.getAll()
    var insertedPlaylistId: Long? = null
    
    fun addPlayList(title: String) {
        if (title.isNotEmpty()) {
            launch { insertedPlaylistId = mPlaylistDao.insert(Playlist(title)) }
        } else {
            App.context.toast("Playlist title is empty!")
        }
    }
    
    fun deletePlaylist(playlist: Playlist) {
        launch { mPlaylistDao.delete(playlist) }
    }
    
    fun deleteAll() {
        playlists.value?.let {
            if (it.isNotEmpty())
                launch { mPlaylistDao.deleteAll() }
        }
    }
}
