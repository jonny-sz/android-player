package com.jonnydev.bmp.adapter

import android.graphics.Color
import android.view.ViewGroup
import com.jonnydev.bmp.databinding.SongListItemBinding
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.TrackList

class PlaylistAdapter(private val mListener: PlaylistAdapterListener, id: Long) :
    TrackAdapter(id),
    TrackTouchHelperCallbackAdapter {
    
    init {
        setHasStableIds(true)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseTrackViewHolder {
        return ViewHolder(getBindingView(parent))
    }
    
    override fun onBindViewHolder(holder: BaseTrackViewHolder, position: Int) {
        val track = mTrackList[position]
        
        with(holder) {
            bind(track)
            itemView.setBackgroundColor(if (track.isSelected) mSelectedTrackColor else Color.WHITE)
        }
    }
    
    override fun setTracks(trackList: TrackList) {
        super.setTracks(trackList)
        mListener.onSetTracks(this)
    }
    
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        with(mTrackList[fromPosition]) {
            mTrackList[fromPosition] = mTrackList[toPosition].apply {
                playlistPosition = fromPosition
                
                if (isSelected) {
                    mTrackList.changeSelectedPosition(fromPosition)
                }
            }
            mTrackList[toPosition] = this.apply { playlistPosition = toPosition }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
    
    override fun onItemSwiped(position: Int) {
        val swipedTrack = mTrackList[position]
        
        if (swipedTrack.isSelected) {
            mTrackList.changeSelectedPosition(-1)
            mListener.onStopPlayer()
        }
        if (position < currentIndex) {
            mTrackList.changeSelectedPosition(currentIndex - 1)
        }
        
        onDeleteTrack(swipedTrack)
    }
    
    private fun onDeleteTrack(track: Track) {
        val position = track.playlistPosition
        val tracksForUpdate = if (position < mTrackList.size - 1) {
            mTrackList.subList(position + 1, mTrackList.size)
        } else {
            emptyList<Track>()
        }
        
        mListener.onDeleteTrack(track, tracksForUpdate)
    }
    
    override fun onItemDragged(startPosition: Int, finalPosition: Int) {
        if (mTrackList[finalPosition].isSelected) {
            mTrackList.changeSelectedPosition(finalPosition)
        }
        
        when (startPosition < finalPosition) {
            true -> mListener.onTrackDragged(mTrackList.subList(startPosition, finalPosition + 1))
            false -> mListener.onTrackDragged(mTrackList.subList(finalPosition, startPosition + 1))
        }
    }
    
    interface PlaylistAdapterListener : TrackAdapterListener {
        fun onDeleteTrack(track: Track, tracksForUpdate: List<Track>)
        fun onTrackDragged(tracksForUpdate: List<Track>)
    }
    
    inner class ViewHolder(bindView: SongListItemBinding) : BaseTrackViewHolder(bindView) {
        init {
            itemView.setOnClickListener {
                val track = mTrackList[adapterPosition]
                
                if (!track.isSelected) {
                    mTrackList.currentIndex = adapterPosition
                    selectItem(track)
                }
                
                mListener.onTrackClick(track, this@PlaylistAdapter)
            }
        }
    }
}
