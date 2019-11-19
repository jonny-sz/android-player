package com.jonnydev.bmp.util

object RequestCodeGen {
    private var count: Int = 0
    
    @Synchronized
    fun generateCode(): Int = ++count
}
