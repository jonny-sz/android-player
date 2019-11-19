package com.jonnydev.bmp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.R
import com.jonnydev.bmp.model.Playlist
import kotlinx.android.synthetic.main.playlist_chooser_item.view.*
import kotlin.properties.Delegates

class PlaylistChooserAdapter(val mListener: PlaylistChooserAdapterListener) :
    RecyclerView.Adapter<PlaylistChooserAdapter.ViewHolder>(),
    AutoUpdatableAdapter,
    PlaylistChooserTouchHelperCallbackAdapter {
    
    init {
        setHasStableIds(true)
    }
    
    var playlists by Delegates.observable(mutableListOf<Playlist>()) { _, oldList, newList ->
        autoNotify(oldList, newList) { oldItem, newItem -> oldItem.id == newItem.id }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_chooser_item, parent, false)
        )
    }
    
    override fun getItemCount() = playlists.size
    
    override fun getItemId(position: Int) = playlists[position].id
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        
        holder.bind(playlist.title)
    }
    
    override fun onItemSwiped(position: Int) {
        mListener.onPlaylistDelete(playlists[position], position)
    }
    
    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        init {
            itemView.setOnClickListener {
                mListener.onPlaylistClick(playlists[adapterPosition])
            }
        }
        
        fun bind(title: String) {
            v.playlist_title.text = title
        }
    }
    
    interface PlaylistChooserAdapterListener {
        fun onPlaylistClick(playlist: Playlist)
        fun onPlaylistDelete(playlist: Playlist, position: Int)
    }
}
