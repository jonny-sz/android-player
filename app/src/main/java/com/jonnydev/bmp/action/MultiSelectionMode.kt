package com.jonnydev.bmp.action

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.jonnydev.bmp.R
import com.jonnydev.bmp.delegates.MultiSelectionTitleDelegate
import com.jonnydev.bmp.util.App

class MultiSelectionMode(private val mListener: MultiSelectionModeListener) {
    private var mActionMode: ActionMode? = null
    var title by MultiSelectionTitleDelegate(mActionMode)
    
    fun start(context: Context) {
        mActionMode = (context as AppCompatActivity).startSupportActionMode(ActionModeCallbacks())
    }
    
    fun finish() {
        mActionMode?.finish()
    }
    
    interface MultiSelectionModeListener {
        fun onCreateActionMode(mode: ActionMode?)
        fun onActionItemClicked()
        fun onDestroyActionMode()
    }
    
    inner class ActionModeCallbacks : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.add(App.context.getString(R.string.add_to_playlist))
            mListener.onCreateActionMode(mode)
            return true
        }
        
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false
        
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            mListener.onActionItemClicked()
            return true
        }
        
        override fun onDestroyActionMode(mode: ActionMode?) {
            mListener.onDestroyActionMode()
        }
    }
}
