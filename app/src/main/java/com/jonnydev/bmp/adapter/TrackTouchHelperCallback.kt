package com.jonnydev.bmp.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TrackTouchHelperCallback(
    private val mAdapter: TrackTouchHelperCallbackAdapter
) : ItemTouchHelper.Callback() {
    
    private var mStartPos = -1
    private var mFinalPos = -1
    
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.END
        
        return makeMovementFlags(dragFlags, swipeFlags)
    }
    
    override fun isItemViewSwipeEnabled() = true
    
    override fun isLongPressDragEnabled() = true
    
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mFinalPos = target.adapterPosition
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        
        return true
    }
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemSwiped(viewHolder.adapterPosition)
    }
    
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> viewHolder?.let { mStartPos = it.adapterPosition }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                if (mStartPos != -1 && mFinalPos != -1) {
                    if (mStartPos != mFinalPos) {
                        mAdapter.onItemDragged(mStartPos, mFinalPos)
                    }
                    
                    mStartPos = -1
                    mFinalPos = -1
                }
            }
        }
    }
}
