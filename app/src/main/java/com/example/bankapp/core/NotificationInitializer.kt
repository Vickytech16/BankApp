package com.example.bankapp.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
object NotificationInitializer {

    const val TRANSACTION_CHANNEL = "notifications"

    fun init(context: Context) {
        val channel = NotificationChannel(
            TRANSACTION_CHANNEL,
            "Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        manager.createNotificationChannel(channel)
    }
}