package com.jonnydev.bmp.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.jonnydev.bmp.extension.toTimeFormat
import java.io.File

@Entity(
    tableName = "track",
    indices = [Index("track_playlist_id")],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlist_id"],
            childColumns = ["track_playlist_id"],
            onDelete = CASCADE
        )
    ]
)
data class Track(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "track_id") var id: Long,
    @ColumnInfo(name = "track_path") var path: String,
    @ColumnInfo(name = "track_name") var fileName: String,
    @ColumnInfo(name = "track_duration") var duration: Long,
    @ColumnInfo(name = "track_position") var playlistPosition: Int = 0,
    @ColumnInfo(name = "track_playlist_id") var playlistId: Long = 0L
) {
    
    @Ignore var isSelected = false
    @Ignore var isMultiSelected = false
    val parent: String
        get() = "/${File(path).parentFile.name}"
    val time: String
        get() = duration.toTimeFormat()
}
