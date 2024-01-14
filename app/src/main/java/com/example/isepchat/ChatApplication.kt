package com.example.isepchat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.isepchat.services.communityService

class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        communityService.getUsers();
        // Add this code in your onCreate or wherever you initialize your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "isepchat"
            val channelName = "ISEP Chat"
            val channelDescription = "ISEP Chat Android App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}