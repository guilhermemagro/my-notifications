## References:
---
https://stackoverflow.com/questions/44597725/cant-get-permission-bind-notification-listener-service-for-notificationlistener
---
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

---
https://stackoverflow.com/a/63772456/11407290
---
From the documentation on how we Inject dependencies into Android classes, we can learn the following:

> Hilt can provide dependencies to other Android classes that have the @AndroidEntryPoint annotation.
> 
> Hilt currently supports the following Android classes:
> 
> `Application` (by using `@HiltAndroidApp`)
> `ViewModel` (by using `@HiltViewModel`)
> `Activity`
> `Fragment`
> `View`
> `Service`
> `BroadcastReceiver`

So when you subclass any of these Android classes, you don't ask Hilt to inject dependencies through the constructors.
Instead, you annotate it with `@AndroidEntryPoint`, and ask Hilt to inject its dependencies by annotating the property with `@Inject`:

```kotlin
    @AndroidEntryPoint
    class ExampleActivity : AppCompatActivity() {
    
        @Inject
        lateinit var mAdapter: SomeAdapter 
        ...
    }
```

So, in your case you should inject `MyRepository` in `MyActivity` and `MyService` like this:

```kotlin
    @AndroidEntryPoint
    class MyService: Service() {
    
        @Inject
        lateinit var myRepository: MyRepository
        ...
    }
    
    @AndroidEntryPoint
    class MyActivity: AppCompatActivity(R.layout.my_layout) {
    
        @Inject
        lateinit var myRepository: MyRepository
        ...
    }
```

And remember:

> Fields injected by Hilt cannot be private

That's it for Android classes that is supported by Hilt.

If you wonder what about classes not supported by Hilt (ex: `ContentProvider`)?!
I recommend learning how from this tutorial @EntryPoint annotation on codelab (also don't forget to 
check the documentation for how to Inject dependencies in classes not supported by Hilt).


---
https://stackoverflow.com/a/49181208/11407290
---

If you're working with Android 7.0+, WhatsApp uses MessageStyle Expanded Notifications.
Here - https://developer.android.com/training/notify-user/expanded.html#message-style

To retrieve all 5 messages from a notification like

> MyFriend (5 messages)
> testt

Do this:
```java
    Bundle extras = mysbn.getNotification().extras;
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)){
    Parcelable b[] = (Parcelable[]) extras.get(Notification.EXTRA_MESSAGES);
    
            if(b != null){
                content = "";
                for (Parcelable tmp : b){
    
                    Bundle msgBundle = (Bundle) tmp;
                    content = content + msgBundle.getString("text") + "\n";
    
                    /*Set<String> io = msgBundle.keySet(); // To get the keys available for this bundle*/
    
                }
            }
        }
```