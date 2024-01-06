package com.example.isepchat

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.isepchat.services.communityService

class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        communityService.getUsers();
    }
}