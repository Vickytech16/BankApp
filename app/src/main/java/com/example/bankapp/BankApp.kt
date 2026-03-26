package com.example.bankapp

import android.app.Application
import android.content.Context
import android.os.Build
import com.example.bankapp.core.NotificationInitializer
import com.example.bankapp.di.AppContainer

class BankApp : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
        initializeNotification(this)
    }
}


private fun initializeNotification(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationInitializer.init(context)
    } else {

    }
}