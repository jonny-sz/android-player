package com.jonnydev.bmp.util

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.jonnydev.bmp.SplashScreenActivity
import kotlin.system.exitProcess


class App : Application() {
    companion object {
        lateinit var context: App
            private set
        lateinit var db: AppDatabase
            private set
        
        fun restart() {
            val intent = Intent(context, SplashScreenActivity::class.java)
            val pendingIntentId = RequestCodeGen.generateCode()
            val pendingIntent = PendingIntent.getActivity(
                context,
                pendingIntentId,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent)
            exitProcess(0)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        context = this
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "bmp-database"
        ).build()
    }
}
