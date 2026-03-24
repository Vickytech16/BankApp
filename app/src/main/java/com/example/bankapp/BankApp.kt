package com.example.bankapp

import android.app.Application
import com.example.bankapp.core.NotificationInitializer
import com.example.bankapp.di.AppContainer

class BankApp : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
        NotificationInitializer.init(this)
    }
}