package com.jonnydev.bmp.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.jonnydev.bmp.databinding.SettingDialogBinding
import com.jonnydev.bmp.prefs.SettingPrefs
import com.jonnydev.bmp.util.Settings

class SettingDialogFragment : AppCompatDialogFragment() {
    private lateinit var mBindingView: SettingDialogBinding
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBindingView = SettingDialogBinding.inflate(LayoutInflater.from(activity)).apply {
            settings = Settings
            btnSettingClose.setOnClickListener { saveSettings() }
        }
        
        return AlertDialog
            .Builder(activity!!)
            .create()
            .apply {
                setView(mBindingView.root)
                setCanceledOnTouchOutside(false)
            }
    }
    
    private fun saveSettings() {
        SettingPrefs.radioId = Settings.playbackModeRadioId
        SettingPrefs.fastForwardTime = Settings.fastForwardTime
        SettingPrefs.apply()
        dismiss()
    }
}
