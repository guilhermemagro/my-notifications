package com.example.mynotifications.data.service

import android.app.Notification
import android.graphics.drawable.Icon
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.mynotifications.data.database.NotificationItem
import com.example.mynotifications.data.repository.NotificationRepository
import com.example.mynotifications.data.utilities.LOG_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TITLE_KEY = "android.title"
private const val TEXT_KEY = "android.text"

@AndroidEntryPoint
class AppNotificationListenerService : NotificationListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var lastNotification: NotificationItem? = null

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onCreate() {
        super.onCreate()
        Log.d("MyNotificationsTag", "AppNotificationListenerService - onCreate()")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        runCatching {
            val sbnExtrasBundle = sbn?.notification?.extras
            val notification = sbn?.notification
            val icon = notification?.smallIcon?.resourceIdOrNull()
            val packageName = sbn?.packageName
            val postTime = sbn?.postTime
            val category = notification?.category

            if (Notification.CATEGORY_SYSTEM != category) {
                tryToInsertNotification(sbnExtrasBundle, packageName, postTime, icon)
            }

            Log.d(LOG_TAG, "extras = " + bundleToString(sbnExtrasBundle))
            Log.d(
                LOG_TAG,
                "onNotificationPosted: Notification = $notification | PackageName = $packageName"
            )
        }.onFailure { error ->
            Log.e(LOG_TAG, "Failed to capture notification from ${sbn?.packageName}", error)
        }
    }

    /**
     * [Icon.getResId] throws for non-resource icons (bitmap, URI, data...), so only read it
     * for [Icon.TYPE_RESOURCE].
     */
    private fun Icon.resourceIdOrNull(): Int? =
        if (type == Icon.TYPE_RESOURCE) resId else null

    private fun tryToInsertNotification(
        extrasBundle: Bundle?,
        packageName: String?,
        postTime: Long?,
        icon: Int?
    ) {
        val title = extrasBundle?.getString(TITLE_KEY)
        val text = extrasBundle?.getString(TEXT_KEY)

        if (!title.isNullOrBlank()
            && !text.isNullOrBlank()
            && !packageName.isNullOrBlank()
            && postTime != null
        ) {
            val notificationItem = NotificationItem(
                title = title,
                message = text,
                packageName = packageName,
                postTime = postTime,
                iconRes = icon
            )
            if (notificationItem != lastNotification) {
                scope.launch {
                    runCatching { notificationRepository.insert(notificationItem) }
                        .onFailure { Log.e(LOG_TAG, "Failed to store notification", it) }
                }
                lastNotification = notificationItem
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

        val notification = sbn?.notification
        val packageName = sbn?.packageName

        Log.d(
            LOG_TAG,
            "onNotificationRemoved: Notification = $notification | PackageName = $packageName"
        )
    }

    private fun bundleToString(bundle: Bundle?): String? {
        if (bundle == null) {
            return null
        }
        var string = "Bundle{"
        for (key in bundle.keySet()) {
            string += " " + key + " => " + bundle[key] + ";"
        }
        string += " }Bundle"
        return string
    }
}

//extras = Bundle{
//android.title => (650) 555-1212;
//android.hiddenConversationTitle => null;
//android.reduced.images => true;
//android.subText => null;
//android.template => android.app.Notification$MessagingStyle;
//android.showChronometer => false;
//android.text => Android is always a sweet treat!;
//android.progress => 0;
//android.progressMax => 0;
//android.selfDisplayName => You;
//android.conversationUnreadMessageCount => 0;
//android.appInfo => ApplicationInfo{42ab3ef com.google.android.apps.messaging};
//android.messages => [Landroid.os.Parcelable;@2db9efc;
//android.showWhen => true;
//android.largeIcon => null;
//android.messagingStyleUser => Bundle[mParcelledData.dataSize=468];
//android.messagingUser => android.app.Person@7ddb8047;
//android.infoText => null;
//android.wearable.EXTENSIONS => Bundle[mParcelledData.dataSize=716];
//android.progressIndeterminate => false;
//android.remoteInputHistory => null;
//android.support.v4.app.extra.COMPAT_TEMPLATE => androidx.core.app.NotificationCompat$MessagingStyle;
//android.isGroupConversation => false;
//}Bundle