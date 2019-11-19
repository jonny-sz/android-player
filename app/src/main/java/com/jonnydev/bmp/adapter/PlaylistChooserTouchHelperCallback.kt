package com.jonnydev.bmp.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class PlaylistChooserTouchHelperCallback(
    val adapter: PlaylistChooserTouchHelperCallbackAdapter
) : ItemTouchHelper.Callback() {
    
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = 0
        val swipeFlags = ItemTouchHelper.END
        
        return makeMovementFlags(dragFlags, swipeFlags)
    }
    
    override fun isItemViewSwipeEnabled() = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemSwiped(viewHolder.adapterPosition)
    }
}
