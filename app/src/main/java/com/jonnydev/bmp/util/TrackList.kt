package com.jonnydev.bmp.util

import com.jonnydev.bmp.model.Track

class TrackList(
    var tracks: MutableList<Track> = mutableListOf(),
    var currentIndex: Int = 0,
    var lastSelectedPosition: Int = -1
) {
    
    var isMultiSelectionMode: Boolean = false
    val currentTrack: Track?
        get() = try {
            tracks[currentIndex]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    
    val nextTrack: Track?
        get() {
            var nextIndex = currentIndex + 1
            val nextTrack: Track
            
            if (tracks.isEmpty())
                return null
            
            when (Settings.playbackMode) {
                PlaybackMode.NO_REPEAT -> if (currentIndex == tracks.lastIndex) return null
                PlaybackMode.REPEAT_ALL -> if (currentIndex == tracks.lastIndex) nextIndex = 0
                PlaybackMode.REPEAT_ONE -> return tracks[currentIndex]
                PlaybackMode.RANDOM -> nextIndex = (0 until tracks.size).random()
            }
            
            nextTrack = tracks[nextIndex]
            currentIndex = nextIndex
            
            return nextTrack
        }
    
    val prevTrack: Track?
        get() {
            var prevIndex = currentIndex - 1
            val prevTrack: Track
            
            if (tracks.isEmpty())
                return null
            
            when (Settings.playbackMode) {
                PlaybackMode.NO_REPEAT,
                PlaybackMode.REPEAT_ALL -> if (currentIndex == 0) return null
                PlaybackMode.REPEAT_ONE -> return tracks[currentIndex]
                PlaybackMode.RANDOM -> prevIndex = (0 until tracks.size).random()
            }
            
            prevTrack = tracks[prevIndex]
            currentIndex = prevIndex
            
            return prevTrack
        }
    
    fun changeSelectedPosition(pos: Int) {
        currentIndex = pos
        lastSelectedPosition = pos
    }
    
    val size: Int
        get() = tracks.size
    
    fun sortByFolder() {
        tracks.sortWith(compareBy({ it.parent.toLowerCase() }, { it.fileName.toLowerCase() }))
    }
    
    fun isNotEmpty() = tracks.isNotEmpty()
    
    fun subList(fromIndex: Int, toIndex: Int) = tracks.subList(fromIndex, toIndex)
    
    operator fun get(index: Int) = tracks[index]
    
    operator fun set(index: Int, value: Track) {
        tracks[index] = value
    }
}
