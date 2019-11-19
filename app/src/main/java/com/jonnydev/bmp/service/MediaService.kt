package com.jonnydev.bmp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.Player


class MediaService : Service() {
    
    private val mediaBinder = MediaBinder()
    private var mAudioManager: AudioManager? = null
    lateinit var player: Player
    var hasStartTrack = false
    val currentPosition: Int
        get() = player.currentPosition
    val duration: Int
        get() = player.duration
    val isPlaying: Boolean
        get() = player.isPlaying
    
    override fun onCreate() {
        super.onCreate()
        
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        player = Player(mAudioManager)
    }
    
    override fun onBind(intent: Intent): IBinder {
        return mediaBinder
    }
    
    override fun onUnbind(intent: Intent): Boolean {
        return true
    }
    
    fun onTrackClick(track: Track) {
        player.setTrack(track)
        player.play()
    }
    
    fun playOrPause() {
        player.playOrPause()
    }
    
    fun play() {
        player.play()
    }
    
    fun stop() {
        player.stop()
    }
    
    fun next(adapter: TrackAdapter) = player.next(adapter)
    
    fun prev(adapter: TrackAdapter) = player.prev(adapter)
    
    fun forward() {
        player.forward()
    }
    
    fun back() {
        player.back()
    }
    
    fun setTrack(track: Track) {
        player.setTrack(track)
    }
    
    fun seekTo(position: Int) {
        player.seekTo(position)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
    
    inner class MediaBinder : Binder() {
        fun getService() = this@MediaService
    }
}
