# Tasks

## 1. Crash fix: resilient icon loading

- [x] 1.1 In `ContextExt.kt`, make `getBitmapFromOrNull` catch `Exception` (not only `NameNotFoundException`) and return `null`. Verify with a unit/instrumented test that a non-existent resource ID in a real package (e.g. `com.android.settings`, id `0x7f999999`) returns `null` instead of throwing.
- [x] 1.2 Add a `packageName` parameter to `DismissibleNotificationItemView` and `NotificationItemView`, pass `notification.packageName` from `HomeScreen`, and use it (not `appName`) for icon resolution. Verify that WhatsApp/Instagram entries now show their icon on a device.
- [x] 1.3 Resolve the icon off the main thread with `produceState` keyed by `(packageName, iconRes)` on `Dispatchers.IO`, backed by an in-memory `LruCache`. Verify that scrolling the list does not log repeated `createPackageContext` calls for the same icon, and that previews still render.

## 2. Crash fix: listener icon type

- [x] 2.1 In `AppNotificationListenerService`, read `smallIcon.resId` only when `smallIcon.type == Icon.TYPE_RESOURCE`, otherwise store `iconRes = null`. Verify by posting a test notification with `Icon.createWithBitmap(...)` (e.g. via a debug button or adb test app): it gets stored without an icon and the service keeps capturing.
- [x] 2.2 Wrap the extraction + insert path in `runCatching` with error logging so one malformed notification cannot stop the listener. Verify that `./gradlew assembleDebug` succeeds and a forced exception in the path only logs.

## 3. Formatting out of the entity

- [x] 3.1 Rewrite `DateExt.kt` using `java.time` `DateTimeFormatter` constants (pt-BR, same `EEEE, dd MMM yyyy` / `HH:mm:ss` patterns). Verify with a unit test in `app/src/test` that a fixed epoch millis formats to the expected strings.
- [x] 3.2 Create `NotificationUiModel` (uid, title, message, packageName, appName, formattedDate, formattedHour, iconRes) and a mapper from `NotificationItem`, moving `knownApplications` into it. Verify with a unit test that `com.whatsapp` maps to "WhatsApp" and that unknown packages map to themselves.
- [x] 3.3 Remove the `@Ignore` computed fields from `NotificationItem` and update all call sites to use the UI model. Verify `./gradlew assembleDebug` succeeds and the Room schema JSON is unchanged.

## 4. Paged home screen

- [x] 4.1 Add `androidx.paging:paging-runtime-ktx`, `androidx.paging:paging-compose` and `androidx.room:room-paging` (latest stable versions compatible with Room 2.8.5 and the Compose BOM) to `app/build.gradle.kts`. Verify that Gradle sync and `./gradlew assembleDebug` succeed.
- [x] 4.2 Add `pagingSource(minPostTime: Long): PagingSource<Int, NotificationItem>` to `NotificationDao` (`WHERE postTime >= :minPostTime ORDER BY uid ASC`) and expose `Flow<PagingData<NotificationItem>>` from `NotificationRepository` via `Pager(PagingConfig(pageSize = 50, enablePlaceholders = true))`, computing the cutoff as `now - NOTIFICATION_RETENTION` in the factory. Remove `getAllNotifications()`. Verify with an instrumented DAO test on an in-memory DB that rows older than the cutoff are excluded and the order is ascending.
- [x] 4.3 In `HomeViewModel`, map to `NotificationUiModel`, insert `DayHeader` separators with `insertSeparators` when the formatted date changes, and `cachedIn(viewModelScope)`. Verify with a unit test (using `androidx.paging:paging-testing` `asSnapshot()`) that items from two days produce two headers in the right positions.
- [x] 4.4 Update `HomeScreen` to `collectAsLazyPagingItems()`, iterating `0 until itemCount` with `peek(i)` to emit `stickyHeader` for headers and `item(key = uid)` for notifications, plus a fixed-height placeholder for nulls. Keep the top buttons and bottom spacer. Verify on a device that headers pin while scrolling, swipe-to-delete and "Delete all" still work, and no "key already used" error occurs.
- [x] 4.5 Update the "go to end" FAB to scroll to the last index based on `itemCount` (with placeholders), using `scrollToItem` if `animateScrollToItem` lands off. Verify on a device with a few hundred notifications that it reaches the newest entry.

## 5. Retention routine (delete notifications older than 2 days)

- [x] 5.1 Add `NOTIFICATION_RETENTION = 2.days` to `Constants.kt` and `deleteOlderThan(minPostTime: Long): Int` (`DELETE ... WHERE postTime < :minPostTime`) to the DAO and repository. Verify with an instrumented DAO test that a row at -49h is deleted and a row at -47h is kept.
- [x] 5.2 Add `androidx.work:work-runtime-ktx`, `androidx.hilt:hilt-work` and kapt `androidx.hilt:hilt-compiler` (latest stable). Verify that `./gradlew assembleDebug` succeeds.
- [x] 5.3 Create `NotificationCleanupWorker` (`@HiltWorker`, `CoroutineWorker`) that calls `deleteOlderThan(now - NOTIFICATION_RETENTION)` and returns `Result.success()`, or `Result.retry()` on failure. Verify with a `work-testing` `TestListenableWorkerBuilder` test that it returns success and calls the repository with the right cutoff.
- [x] 5.4 Make `MyApplication` implement `Configuration.Provider` with an injected `HiltWorkerFactory`, and remove WorkManager's default initializer in `AndroidManifest.xml` (`InitializationProvider` / `WorkManagerInitializer` with `tools:node="remove"`). Verify the app launches without a crash and logcat shows no WorkManager initialization error.
- [x] 5.5 In `MyApplication.onCreate()`, enqueue a 6h `PeriodicWorkRequest` with `enqueueUniquePeriodicWork("notification-cleanup", ExistingPeriodicWorkPolicy.KEEP, ...)`. Also fix the misleading log message there. Verify via `adb shell dumpsys jobscheduler | grep mynotifications` (or WorkManager's diagnostics) that exactly one periodic job exists after launching the app several times.

## 6. Integration check

- [x] 6.1 On a device with a seeded large DB (e.g. a debug-only seeding of 5,000 rows spread over 5 days, including a row with an invalid `iconRes`), open the app and verify: no crash, only the last 2 days are listed, headers group correctly, and after triggering the worker (`adb shell cmd jobscheduler run -f com.example.mynotifications <jobId>`) rows older than 2 days are gone from the DB.
