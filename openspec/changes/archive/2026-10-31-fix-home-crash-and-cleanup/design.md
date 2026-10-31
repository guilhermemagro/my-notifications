# Design

## Context

See proposal.md (Why) for the problems. Current state that shapes the approach:

- `NotificationDao.getAllNotifications()` returns `Flow<List<NotificationItem>>` over `SELECT *` ordered by `uid ASC`. `HomeViewModel` groups the result by `formattedDate` on whichever dispatcher collects it, which is Main via `collectAsState`.
- `NotificationItem` computes `appName`, `formattedDate` and `formattedHour` as `@Ignore` fields in the constructor, creating two `SimpleDateFormat` instances per row on every emission.
- `Context.getBitmapFromOrNull(id, packageName)` runs inside composition, receives `appName` instead of `packageName`, and only catches `NameNotFoundException`.
- `AppNotificationListenerService` reads `notification.smallIcon.resId` unconditionally.
- The app uses Hilt with kapt, Room 2.8.5 with Java codegen (kapt), and Compose with a BOM. There is no WorkManager or Paging yet. DB version is 1 with exported schemas.

## Goals / Non-Goals

**Goals:**
- Remove every code path where a bad icon resource can crash the home screen or the listener.
- Keep memory and CPU usage on home screen open bounded, independent of table size.
- Add a reliable 2-day retention policy.

**Non-Goals:**
- Changing the visual design of list items or headers.
- Database schema changes (no new index or columns). After retention the table stays small, so an index on `postTime` is not needed now.
- Caching icon bitmaps across process restarts or on disk.
- Making the retention period configurable by the user.

## Decisions

### 1. Icon loading: catch broadly, resolve by package name, resolve off the main thread, remember the result
- `getBitmapFromOrNull` catches `Exception` (covers `NameNotFoundException`, `Resources.NotFoundException`, `SecurityException`, and `IllegalArgumentException`/OOM-adjacent failures from `toBitmapOrNull`) and returns `null`.
- The view receives `packageName` separately from `appName`. `DismissibleNotificationItemView` and `NotificationItemView` get a new `packageName` parameter.
- In the composable, resolve the bitmap with `produceState(initialValue = null, iconRes, packageName)` on `Dispatchers.IO`. It runs once per key, so recomposition does not re-decode, and `createPackageContext`/decode do not run on Main.
- Add a small in-memory `LruCache<Pair<String, Int>, ImageBitmap>` so the same app icon is not decoded for every row. Keep it in a singleton icon loader injected via Hilt, or in a file-level object.
- *Alternative considered:* Only adding `catch (Resources.NotFoundException)`. Rejected because other exception types from foreign package contexts are equally possible, and this function should never be fatal.

### 2. Paging 3 with Room's `PagingSource`, placeholders enabled
- DAO: `@Query("SELECT * FROM notificationitem WHERE postTime >= :minPostTime ORDER BY uid ASC") fun pagingSource(minPostTime: Long): PagingSource<Int, NotificationItem>` (requires `room-paging`).
- The repository exposes `Flow<PagingData<NotificationItem>>` built from `Pager(PagingConfig(pageSize = 50, enablePlaceholders = true))`.
- The ViewModel maps items into a UI model and calls `insertSeparators` to emit a `DayHeader(date)` between items whose formatted date differs, plus one before the first item. The result is `cachedIn(viewModelScope)`.
- UI: `collectAsLazyPagingItems()`. Inside `LazyColumn`, iterate `0 until itemCount` and use `peek(i)`: a header becomes a regular `item(key = "header_$firstUid")`, a notification becomes `item(key = uid)`, and the content calls `items[i]` to trigger loads.
- The pinned day header is an overlay drawn above the list, showing the date of the first visible loaded item (`derivedStateOf` over `visibleItemsInfo` + `peek`). LazyColumn's `stickyHeader` was tried first and failed on the emulator: after "go to end" jumps, the day's header sits in a not-yet-loaded placeholder region, so it is never registered and nothing is pinned. The overlay works whether or not that region is loaded.
- **Placeholders enabled** so `itemCount` reflects the full result set. "Go to end" then scrolls to `itemCount - 1 + offset` and Room's PagingSource jumps there (jump support) without loading every page in between.
- `minPostTime` is computed as `now - 2 days` when the Pager's `pagingSourceFactory` is invoked. Room invalidates the source on every table change, so the cutoff refreshes naturally. Together with the worker, this satisfies "not shown between runs".
- *Alternative considered:* Keep `Flow<List>` and add `LIMIT`. Rejected because it would silently hide history and still reload the full window on every insert.
- *Alternative considered:* Reverse the order to newest-first and drop "go to end". Rejected because it changes established UX.

### 3. Move formatting out of the entity
- Remove the `@Ignore` computed fields from `NotificationItem` so it becomes a plain row.
- Create a UI model (`NotificationUiModel`: uid, title, message, packageName, appName, formattedHour, formattedDate, iconRes) mapped in the ViewModel's `PagingData.map`, which runs off Main inside Paging's pipeline.
- Replace per-call `SimpleDateFormat` creation with `java.time` `DateTimeFormatter` constants (thread-safe, minSdk 28 supports `java.time`) in `DateExt.kt`. The pt-BR locale and existing patterns stay the same.
- Keep `knownApplications` mapping in the UI mapper.

### 4. Listener: guard icon type
- `val icon = notification?.smallIcon?.takeIf { it.type == Icon.TYPE_RESOURCE }?.resId`.
- Additionally wrap the extraction/insert path in `runCatching` with a log, so one malformed notification can never kill the listener.

### 5. Retention via WorkManager periodic worker with Hilt
- `NotificationCleanupWorker` (`@HiltWorker`, `CoroutineWorker`) calls `repository.deleteOlderThan(System.currentTimeMillis() - 2.days)`.
- DAO: `@Query("DELETE FROM notificationitem WHERE postTime < :minPostTime") suspend fun deleteOlderThan(minPostTime: Long): Int`.
- Schedule from `MyApplication.onCreate()` with `enqueueUniquePeriodicWork("notification-cleanup", ExistingPeriodicWorkPolicy.KEEP, PeriodicWorkRequest(6h))`. KEEP makes it idempotent. WorkManager persists across reboot.
- Hilt integration: `MyApplication` implements `Configuration.Provider` with an injected `HiltWorkerFactory`. Remove the default `WorkManagerInitializer` via a `tools:node="remove"` entry for `androidx.startup.InitializationProvider` in the manifest.
- Retention constant `NOTIFICATION_RETENTION = 2.days` lives in `Constants.kt` and is shared by the worker and the paging cutoff.
- *Alternative considered:* Delete old rows inside the listener on every insert. Rejected because it doesn't run if no notifications arrive and it couples capture with retention. The paging cutoff already covers display.
- *Alternative considered:* Interval of 24h. 6h keeps the table tighter at negligible cost. The display filter makes the exact interval non-critical anyway.

## Risks / Trade-offs

- [Overlay header doesn't get the "push" animation of a native sticky header when the next day's header arrives] → Accepted. The header text simply switches when the next day's first item reaches the top.
- [Placeholders + `animateScrollToItem` to an unloaded position may land slightly off after load] → Use `scrollToItem` for the jump, or animate after the page loads. Validate manually with a large dataset.
- [Removing `@Ignore` fields changes `NotificationItem`'s public shape] → `@Ignore` fields are not in the schema, so there is no migration. Update all call sites at compile time.
- [`equals`/`hashCode` on `NotificationItem` are inconsistent (hashCode uses `uid` and exact `postTime`, equals uses a 1s tolerance)] → Out of scope, but don't rely on it in `DiffUtil`. Paging's diff should compare by `uid`.
- [First cleanup deletes all existing history older than 2 days] → Intended by the user. Note it in release notes.
- [Removing WorkManager's default initializer incorrectly crashes at startup] → Cover it with a manual launch check in tasks.

## Migration Plan

No DB migration. Ship the change as a normal update. On first launch the worker is enqueued and, on its first run, purges rows older than 2 days. To roll back, revert the change. Already-deleted rows are not recoverable.
