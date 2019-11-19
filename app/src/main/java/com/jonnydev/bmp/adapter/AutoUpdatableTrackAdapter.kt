package com.jonnydev.bmp.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.TrackList

interface AutoUpdatableTrackAdapter {
    fun RecyclerView.Adapter<*>.autoNotify(
        oldData: TrackList, newData: TrackList, compare: (Track, Track) -> Boolean
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(oldData[oldItemPosition], newData[newItemPosition])
            }
            
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldData[oldItemPosition] == newData[newItemPosition]
            }
            
            override fun getOldListSize() = oldData.size
            
            override fun getNewListSize() = newData.size
        })
        
        diff.dispatchUpdatesTo(this)
    }
}