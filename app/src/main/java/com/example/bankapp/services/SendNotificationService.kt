package com.example.bankapp.services

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.bankapp.R
import com.example.bankapp.entities.Notification

object SendNotificationService {
    private const val OTP_NOTIFICATION_ID = 1001
    fun showNotification(
        context: Context,
        notification: Notification,
        isOtp: Boolean = true
    ) {
        val notification = NotificationCompat.Builder(context, "notifications")
            .setSmallIcon(R.drawable.bank_logo)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = if (isOtp) OTP_NOTIFICATION_ID else System.currentTimeMillis().toInt()

        notificationManager.notify(
            notificationId,
            notification
        )
    }
}