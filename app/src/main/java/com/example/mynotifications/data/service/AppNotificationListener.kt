package com.example.mynotifications.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class AppNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn?.notification
        val packageName = sbn?.packageName

        Log.d(
            "MyNotifications",
            "onNotificationPosted: Notification = $notification | PackageName = $packageName"
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

        val notification = sbn?.notification
        val packageName = sbn?.packageName

        Log.d(
            "MyNotifications",
            "onNotificationRemoved: Notification = $notification | PackageName = $packageName"
        )
    }
}