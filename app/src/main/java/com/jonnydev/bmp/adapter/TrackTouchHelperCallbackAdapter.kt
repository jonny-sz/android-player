package com.jonnydev.bmp.adapter

interface TrackTouchHelperCallbackAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemSwiped(position: Int)
    fun onItemDragged(startPosition: Int, finalPosition: Int)
}
