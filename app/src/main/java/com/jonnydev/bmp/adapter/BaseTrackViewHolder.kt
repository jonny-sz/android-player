package com.jonnydev.bmp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.databinding.SongListItemBinding
import com.jonnydev.bmp.model.Track

abstract class BaseTrackViewHolder(private val bindView: SongListItemBinding) :
    RecyclerView.ViewHolder(bindView.root) {
    
    fun bind(track: Track) {
        bindView.track = track
    }
}
