package com.jonnydev.bmp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.R
import com.jonnydev.bmp.databinding.SongListItemBinding
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.App
import com.jonnydev.bmp.util.TrackList
import kotlin.properties.Delegates

abstract class TrackAdapter(val id: Long) :
    RecyclerView.Adapter<BaseTrackViewHolder>(),
    AutoUpdatableTrackAdapter {
    
    protected var mTrackList by Delegates.observable(TrackList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { oldItem, newItem -> oldItem.id == newItem.id }
    }
    protected val mSelectedTrackColor =
        ContextCompat.getColor(App.context, R.color.selectedTrackColor)
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(App.context)
    val currentTrack: Track?
        get() = mTrackList.currentTrack
    val currentIndex: Int
        get() = mTrackList.currentIndex
    
    protected fun getBindingView(parent: ViewGroup): SongListItemBinding =
        SongListItemBinding.inflate(mLayoutInflater, parent, false)
    
    override fun getItemCount() = mTrackList.size
    
    override fun getItemId(position: Int) = mTrackList[position].id
    
    fun selectItem(track: Track) {
        with(mTrackList) {
            if (lastSelectedPosition >= 0) {
                get(lastSelectedPosition).isSelected = false
                notifyItemChanged(lastSelectedPosition)
            }
            
            track.isSelected = true
            lastSelectedPosition = currentIndex
            notifyItemChanged(currentIndex)
        }
    }
    
    open fun setTracks(trackList: TrackList) {
        this.mTrackList = trackList
    }
    
    fun getTrackById(id: Long): Track? = mTrackList.tracks.find { it.id == id }
    
    fun selectTrackByPosition(position: Int) {
        val track = mTrackList[position]
        mTrackList.currentIndex = position
        selectItem(track)
    }
    
    fun getPositionByTrack(track: Track) = mTrackList.tracks.indexOf(track)
    
    fun getTrackByPosition(position: Int) = mTrackList[position]
    
    fun getNext() = mTrackList.nextTrack.also { nextTrack ->
        nextTrack?.let { selectItem(it) }
    }
    
    fun getPrev() = mTrackList.prevTrack.also { prevTrack ->
        prevTrack?.let { selectItem(it) }
    }
    
    fun isNotEmpty() = mTrackList.isNotEmpty()
    
    fun sortByFolder() {
        mTrackList.sortByFolder()
    }
}
