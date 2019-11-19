package com.jonnydev.bmp.delegates

import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.util.ConnMediator
import kotlin.reflect.KProperty

class TrackAdapterDelegate(private val mediator: ConnMediator) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): TrackAdapter? {
        return mediator.trackAdapter
    }
    
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TrackAdapter?) {
        mediator.trackAdapter = value
    }
}