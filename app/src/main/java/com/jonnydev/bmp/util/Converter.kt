package com.jonnydev.bmp.util

import com.jonnydev.bmp.extension.seconds
import com.jonnydev.bmp.extension.toTimeFormat

object Converter {
    @JvmStatic
    fun millisecondsToText(value: Int) = value.seconds.toString()
    
    @JvmStatic
    fun millisecondsToSeconds(value: Int) = value.seconds
    
    @JvmStatic
    fun numberToTimeFormat(value: Int) = value.toTimeFormat()
    
    @JvmStatic
    fun numberToTimeFormat(value: Long) = value.toTimeFormat()
}
