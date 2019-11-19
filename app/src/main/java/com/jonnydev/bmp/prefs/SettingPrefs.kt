package com.jonnydev.bmp.prefs

import android.content.Context
import com.jonnydev.bmp.util.App
import com.jonnydev.bmp.util.Settings

object SettingPrefs {
    private const val FILE_NAME = "com.jonnydev.bmp.prefs.setting_prefs"
    private const val STORAGE_RADIO_ID_KEY = "$FILE_NAME.radio_id"
    private const val STORAGE_FAST_FORWARD_TIME_KEY = "$FILE_NAME.fast_forward_time"
    
    private val mPrefs by lazy { App.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE) }
    private val mEditor = mPrefs.edit()
    
    var radioId: Int
        get() = PrefsUtils.getInt(STORAGE_RADIO_ID_KEY, Settings.playbackModeRadioId, mPrefs)
        set(value) = PrefsUtils.putInt(STORAGE_RADIO_ID_KEY, value, mEditor)
    
    var fastForwardTime: Int
        get() = PrefsUtils.getInt(STORAGE_FAST_FORWARD_TIME_KEY, Settings.fastForwardTime, mPrefs)
        set(value) = PrefsUtils.putInt(STORAGE_FAST_FORWARD_TIME_KEY, value, mEditor)
    
    fun apply() {
        mEditor.apply()
    }
}
