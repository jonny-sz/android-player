package com.jonnydev.bmp.util

import android.media.AudioManager
import android.media.MediaPlayer
import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.prefs.TrackPrefs
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext

class Player(private val mAudioManager: AudioManager?) : MediaPlayer(), CoroutineScope {
    var isNotStopped = false
        private set
    private var mJob: Job? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    
    private var mPlayerListener: OnPlayerUpdateListener? = null
    private var mHasAudioFocus = false
    private val mOnAudioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    pause()
                    mPlayerListener?.onClickPause()
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    mHasAudioFocus = true
                }
            }
        }
    
    fun setTrack(track: Track) {
        if (!File(track.path).exists()) {
            mPlayerListener?.onTrackError(track)
            return
        }
    
        saveTrackState(track)
        reset()
        setDataSource(track.path)
        prepare()
    }
    
    private fun saveTrackState(track: Track) {
        with(TrackPrefs) {
            playlistId = track.playlistId
            trackId = track.id
            apply()
        }
    }
    
    fun playOrPause() {
        when (isPlaying) {
            true -> {
                pause()
                mJob?.cancel()
                mAudioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
                mHasAudioFocus = false
                mPlayerListener?.onClickPause()
            }
            else -> {
                play()
            }
        }
    }
    
    fun prev(adapter: TrackAdapter): Track? {
        when {
            !isNotStopped -> return null
            currentPosition in 0..2000 -> return adapter.getPrev().also { changeTrack(it) }
            else -> {
                seekTo(0)
                if (!isPlaying)
                    play()
            }
        }
        
        return null
    }
    
    fun next(adapter: TrackAdapter): Track? {
        if (!isNotStopped) return null
        
        return adapter.getNext().also { changeTrack(it) }
    }
    
    fun forward() {
        if (isNotStopped && currentPosition != duration) {
            var resultPosition = currentPosition + Settings.fastForwardTime
            
            if (resultPosition > duration) {
                resultPosition = duration
            }
            
            moveToPosition(resultPosition)
        }
    }
    
    fun back() {
        if (isNotStopped && currentPosition != 0) {
            var resultPosition = currentPosition - Settings.fastForwardTime
            
            if (resultPosition < 0) {
                resultPosition = 0
            }
            
            moveToPosition(resultPosition)
        }
    }
    
    private fun moveToPosition(resultPosition: Int) {
        seekTo(resultPosition)
        mPlayerListener?.updateProgress(currentPosition)
    }
    
    private fun changeTrack(track: Track?) {
        if (track != null) {
            setTrack(track)
            play()
        }
    }
    
    fun play() {
        if (isNotStopped) {
            mJob?.cancel()
            
            checkAudioFocus()
            
            if (mHasAudioFocus) {
                start()
                syncUpdateSeekBar()
                mPlayerListener?.onClickPlay()
            }
        }
    }
    
    override fun prepare() {
        super.prepare()
        
        isNotStopped = true
    }
    
    override fun stop() {
        super.stop()
        mJob?.cancel()
        mAudioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
        mHasAudioFocus = false
        isNotStopped = false
    }
    
    private fun checkAudioFocus() {
        if (!mHasAudioFocus) {
            val result = mAudioManager?.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mHasAudioFocus = true
            }
        }
    }
    
    private fun syncUpdateSeekBar() {
        mJob = launch {
            mPlayerListener?.updateProgress(currentPosition)
            
            while (isPlaying) {
                mPlayerListener?.updateProgress(currentPosition)
                delay(200)
                TrackPrefs.playerPosition = currentPosition
                TrackPrefs.apply()
            }
        }
    }
    
    override fun release() {
        mJob?.cancel()
        mAudioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
        super.release()
        
    }
    
    fun setOnPlayerUpdateListener(onPlayerUpdateListener: OnPlayerUpdateListener) {
        mPlayerListener = onPlayerUpdateListener
    }
    
    interface OnPlayerUpdateListener {
        fun onClickPlay()
        fun onClickPause()
        fun updateProgress(playerCurrentPosition: Int)
        fun onTrackError(track: Track)
    }
}
