package com.jonnydev.bmp.viewmodel

import com.jonnydev.bmp.util.App
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel : AsyncViewModel() {
    private val mTrackDao = App.db.trackDao()
    
    fun updateTracksAndThen(time: Long?, block: () -> Unit) {
        launch {
            val timer: Job? = time?.let { launch { delay(it) } }
            
            mTrackDao.updateAllTracks(this).join()
            timer?.join()
            block()
        }
    }
}
