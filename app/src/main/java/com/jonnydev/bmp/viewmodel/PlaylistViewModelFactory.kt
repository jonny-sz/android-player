package com.jonnydev.bmp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class PlaylistViewModelFactory(private val playlistId: Long) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaylistViewModel(playlistId) as T
    }
}
