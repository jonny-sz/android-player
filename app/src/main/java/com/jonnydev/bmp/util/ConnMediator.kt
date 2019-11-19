package com.jonnydev.bmp.util

import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.service.MediaService
import kotlin.properties.Delegates.observable

class ConnMediator {
    var onConnectionsReady: (() -> Unit)? = null
    private var mIsConnectionsReady: Boolean by observable(false) { _, _, newValue ->
        if (newValue) onConnectionsReady?.invoke()
    }
    private val lock = Any()
    
    var mediaService: MediaService? = null
        set(value) {
            synchronized(lock) {
                if (trackAdapter != null && field == null) {
                    field = value
                    mIsConnectionsReady = true
                } else {
                    field = value
                }
            }
        }
    
    var trackAdapter: TrackAdapter? = null
        set(value) {
            synchronized(lock) {
                if (mediaService != null && field == null) {
                    field = value
                    mIsConnectionsReady = true
                } else {
                    field = value
                }
            }
        }
}
