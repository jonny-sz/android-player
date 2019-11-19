package com.jonnydev.bmp.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.jonnydev.bmp.databinding.SongListItemBinding
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.TrackList
import kotlinx.android.synthetic.main.song_list_item.view.*

class AllTracksAdapter(private var mListener: AllTracksAdapterListener) : TrackAdapter(0L) {
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
            itemView.checked.visibility = if (track.isMultiSelected) View.VISIBLE else View.GONE
        }
    }
    
    override fun setTracks(trackList: TrackList) {
        super.setTracks(trackList)
        mListener.onSetTracks(this)
    }
    
    private fun multiSelectItem(track: Track, view: View) {
        track.isMultiSelected = !track.isMultiSelected
        view.visibility = if (track.isMultiSelected) View.VISIBLE else View.GONE
        
        if (track.isMultiSelected) {
            mListener.onAddSelectedTrack(track)
        } else {
            mListener.onRemoveSelectedTrack(track)
        }
    }
    
    interface AllTracksAdapterListener : TrackAdapterListener {
        fun onAddSelectedTrack(track: Track)
        fun onRemoveSelectedTrack(track: Track)
        fun onStartMultiSelectionMode()
    }
    
    inner class ViewHolder(bindView: SongListItemBinding) : BaseTrackViewHolder(bindView) {
        init {
            itemView.setOnClickListener { view ->
                val track = mTrackList[adapterPosition]
                
                if (!mTrackList.isMultiSelectionMode) {
                    if (!track.isSelected) {
                        mTrackList.currentIndex = adapterPosition
                        selectItem(track)
                    }
                    
                    mListener.onTrackClick(track, this@AllTracksAdapter)
                } else {
                    multiSelectItem(track, view.checked)
                }
            }
            itemView.setOnLongClickListener { view ->
                if (!mTrackList.isMultiSelectionMode) {
                    mListener.onStartMultiSelectionMode()
                    multiSelectItem(mTrackList[adapterPosition], view.checked)
                }
                
                true
            }
        }
    }
}
