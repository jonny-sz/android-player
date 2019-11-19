package com.jonnydev.bmp.adapter

import com.jonnydev.bmp.model.Track

interface TrackAdapterListener {
    fun onSetTracks(adapter: TrackAdapter)
    fun onTrackClick(track: Track, adapter: TrackAdapter)
    fun onStopPlayer()
}
