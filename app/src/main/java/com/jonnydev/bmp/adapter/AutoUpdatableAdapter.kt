package com.jonnydev.bmp.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

interface AutoUpdatableAdapter {
    fun <T> RecyclerView.Adapter<*>.autoNotify(
        oldData: List<T>, newData: List<T>, compare: (T, T) -> Boolean
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
