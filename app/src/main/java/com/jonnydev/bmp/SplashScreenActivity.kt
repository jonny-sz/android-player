package com.jonnydev.bmp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jonnydev.bmp.prefs.TrackPrefs
import com.jonnydev.bmp.viewmodel.SplashScreenViewModel

class SplashScreenActivity : AppCompatActivity() {
    private val mViewModel by lazy {
        ViewModelProvider(this)[SplashScreenViewModel::class.java]
    }
    private var mMinLoadingTime: Long? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (TrackPrefs.isUpdating) {
            setContentView(R.layout.activity_splash_screen_upd)
            TrackPrefs.isUpdating = false
            TrackPrefs.apply()
            mMinLoadingTime = 1000
        } else {
            setContentView(R.layout.activity_splash_screen)
            mMinLoadingTime = 1500
        }
        
        mViewModel.updateTracksAndThen(mMinLoadingTime) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
