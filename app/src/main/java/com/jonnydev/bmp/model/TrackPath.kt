package com.jonnydev.bmp.model

import androidx.room.ColumnInfo

data class TrackPath(
    @ColumnInfo(name = "track_id") var trackId: Long,
    @ColumnInfo(name = "track_path") var path: String
)
