package com.jonnydev.bmp.delegates

import com.jonnydev.bmp.service.MediaService
import com.jonnydev.bmp.util.ConnMediator
import kotlin.reflect.KProperty

class MediaServiceDelegate(private val mediator: ConnMediator) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): MediaService? {
        return mediator.mediaService
    }
    
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MediaService?) {
        mediator.mediaService = value
    }
}
