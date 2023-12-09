package com.example.mynotifications.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.mynotifications.data.database.NotificationItem
import com.example.mynotifications.data.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppNotificationListenerService : NotificationListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onCreate() {
        super.onCreate()
        Log.d("MyNotificationsTag", "AppNotificationListenerService - onCreate()")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        // TODO - Ignore if has the same contents of the last notification
        val notification = sbn?.notification
        val packageName = sbn?.packageName
//        sbn?.postTime // horario timestamp - 1702158893946
//        sbn?.notification?.tickerText // titulo

        scope.launch {
            notificationRepository.insert(NotificationItem(
                title = packageName ?: "Title",
                message = "Message"
            ))
        }
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