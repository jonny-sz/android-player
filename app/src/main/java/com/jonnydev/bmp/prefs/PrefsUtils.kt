package com.jonnydev.bmp.prefs

import android.content.SharedPreferences

object PrefsUtils {
    private val mOldLongPrefs = mutableMapOf<String, Long>()
    private val mOldIntPrefs = mutableMapOf<String, Int>()
    private val mOldBooleanPrefs = mutableMapOf<String, Boolean>()
    
    fun getLong(key: String, defValue: Long, prefs: SharedPreferences) =
        prefs.getLong(key, defValue).also { mOldLongPrefs[key] = it }
    
    fun putLong(key: String, newValue: Long, editor: SharedPreferences.Editor) {
        if (newValue != mOldLongPrefs[key]) {
            mOldLongPrefs[key] = newValue
            editor.putLong(key, newValue)
        }
    }
    
    fun getInt(key: String, defValue: Int, prefs: SharedPreferences) =
        prefs.getInt(key, defValue).also { mOldIntPrefs[key] = it }
    
    fun putInt(key: String, newValue: Int, editor: SharedPreferences.Editor) {
        if (newValue != mOldIntPrefs[key]) {
            mOldIntPrefs[key] = newValue
            editor.putInt(key, newValue)
        }
    }
    
    fun getBoolean(key: String, defValue: Boolean, prefs: SharedPreferences) =
        prefs.getBoolean(key, defValue).also { mOldBooleanPrefs[key] = it }
    
    fun putBoolean(key: String, newValue: Boolean, editor: SharedPreferences.Editor) {
        if (newValue != mOldBooleanPrefs[key]) {
            mOldBooleanPrefs[key] = newValue
            editor.putBoolean(key, newValue)
        }
    }
}