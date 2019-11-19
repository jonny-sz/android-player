package com.jonnydev.bmp.util

import android.net.Uri
import android.provider.MediaStore
import com.jonnydev.bmp.model.Track

object AudioUtils {
    private val mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val mProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION
    )
    
    fun fetchTracks(): MutableList<Track> {
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        return getTracksByUri(mAudioUri, selection)
    }
    
    private fun getTracksByUri(
        uri: Uri,
        selection: String,
        selectionArgs: Array<String>? = null
    ) = mutableListOf<Track>().apply {
        App.context.contentResolver.query(uri, mProjection, selection, selectionArgs, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        add(
                            Track(
                                id = cursor.getLong(cursor.getColumnIndex(mProjection[0])),
                                path = cursor.getString(cursor.getColumnIndex(mProjection[1])),
                                fileName = cursor.getString(cursor.getColumnIndex(mProjection[2])),
                                duration = cursor.getLong(cursor.getColumnIndex(mProjection[3]))
                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
    }
}
