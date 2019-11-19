package com.jonnydev.bmp.util

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.jonnydev.bmp.BR
import com.jonnydev.bmp.R

object Settings : BaseObservable() {
    @get:Bindable
    var playbackModeRadioId = R.id.radio_no_repeat
        set(value) {
            field = value
            when (value) {
                R.id.radio_no_repeat -> playbackMode = PlaybackMode.NO_REPEAT
                R.id.radio_repeat_all -> playbackMode = PlaybackMode.REPEAT_ALL
                R.id.radio_repeat_one -> playbackMode = PlaybackMode.REPEAT_ONE
                R.id.radio_random -> playbackMode = PlaybackMode.RANDOM
            }
        }
    
    var playbackMode = PlaybackMode.NO_REPEAT
    
    @get:Bindable
    var fastForwardTime = 5_000
        set(value) {
            field = value
            notifyPropertyChanged(BR.fastForwardTime)
        }
    
    fun addSecond() {
        if (fastForwardTime < 10_000)
            fastForwardTime += 1_000
    }
    
    fun subtractSecond() {
        if (fastForwardTime > 1_000)
            fastForwardTime -= 1_000
    }
}
