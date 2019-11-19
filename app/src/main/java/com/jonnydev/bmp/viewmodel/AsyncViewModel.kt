package com.jonnydev.bmp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class AsyncViewModel : ViewModel(), CoroutineScope {
    protected val mJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + mJob
    
    override fun onCleared() {
        super.onCleared()
        mJob.cancel()
    }
}
