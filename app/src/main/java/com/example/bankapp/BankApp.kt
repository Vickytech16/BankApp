package com.example.bankapp

import android.app.Application
import android.app.NotificationChannel
import com.example.bankapp.core.notifications.NotificationInitializer
import com.example.bankapp.di.AppContainer
import dagger.hilt.android.HiltAndroidApp

class BankApp : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
        NotificationInitializer.init(this)
    }
}