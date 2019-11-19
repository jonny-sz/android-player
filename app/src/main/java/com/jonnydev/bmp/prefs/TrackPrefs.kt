package com.jonnydev.bmp.prefs

import android.content.Context
import com.jonnydev.bmp.util.App

object TrackPrefs {
    private const val FILE_NAME = "com.jonnydev.bmp.prefs.track_prefs"
    private const val STORAGE_PLAYLIST_ID_KEY = "$FILE_NAME.playlist_id"
    private const val STORAGE_TRACK_ID_KEY = "$FILE_NAME.track_id"
    private const val STORAGE_PLAYER_POSITION_KEY = "$FILE_NAME.player_position"
    private const val STORAGE_IS_UPDATING_KEY = "$FILE_NAME.is_updating"
    private const val STORAGE_IS_PLAYING_KEY = "$FILE_NAME.is_playing"
    const val DEF_PLAYLIST_ID = 0L
    const val DEF_TRACK_ID = -1L
    const val DEF_PLAYER_POS = 0
    const val DEF_IS_UPDATING = false
    const val DEF_IS_PLAYING = false
    
    private val mPrefs by lazy { App.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE) }
    private val mEditor = mPrefs.edit()
    
    var playlistId: Long
        get() = PrefsUtils.getLong(STORAGE_PLAYLIST_ID_KEY, DEF_PLAYLIST_ID, mPrefs)
        set(value) = PrefsUtils.putLong(STORAGE_PLAYLIST_ID_KEY, value, mEditor)
    
    var trackId: Long
        get() = PrefsUtils.getLong(STORAGE_TRACK_ID_KEY, DEF_TRACK_ID, mPrefs)
        set(value) = PrefsUtils.putLong(STORAGE_TRACK_ID_KEY, value, mEditor)
    
    var playerPosition: Int
        get() = PrefsUtils.getInt(STORAGE_PLAYER_POSITION_KEY, DEF_PLAYER_POS, mPrefs)
        set(value) = PrefsUtils.putInt(STORAGE_PLAYER_POSITION_KEY, value, mEditor)
    
    var isUpdating: Boolean
        get() = PrefsUtils.getBoolean(STORAGE_IS_UPDATING_KEY, DEF_IS_UPDATING, mPrefs)
        set(value) = PrefsUtils.putBoolean(STORAGE_IS_UPDATING_KEY, value, mEditor)
    
    var isPlaying: Boolean
        get() = PrefsUtils.getBoolean(STORAGE_IS_PLAYING_KEY, DEF_IS_PLAYING, mPrefs)
        set(value) = PrefsUtils.putBoolean(STORAGE_IS_PLAYING_KEY, value, mEditor)
    
    fun clear(): TrackPrefs {
        mEditor.clear()
        
        return this
    }
    
    fun apply() {
        mEditor.apply()
    }
    
    fun commit() {
        mEditor.commit()
    }
}
