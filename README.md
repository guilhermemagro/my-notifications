## References:

https://stackoverflow.com/questions/44597725/cant-get-permission-bind-notification-listener-service-for-notificationlistener

This specific permission must be granted manually by the user in android Settings, after that your 
service will be executed as you bound the permission to your service in AndroidManifest.xml with 
this:

```xml
    <service android:name=".PcNotification"
    android:label="PCNotificationService"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
        <intent-filter>
            <action android:name="android.service.notification.NotificationListenerService" />
        </intent-filter>
    </service>
```

The problem is that most users won't know where to grant this permission (e.g inside android
Settings), so you can open it for them with this:
```kotlin
    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
```

