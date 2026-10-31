# Proposal

## Why

After a few days of use, the app crashes when the home screen opens. Reading the code turned up one likely cause and several things that get worse as the database grows:

- Loading an icon throws an uncaught `Resources.NotFoundException` when the resource ID stored for another app's icon no longer exists, for example after that app updates.
- The home screen loads and re-processes the whole notification table on every new notification, and nothing ever trims the table.
- The listener service reads `smallIcon.resId` without checking the icon type, which throws for non-resource icons.

Notifications older than 2 days have no value to the user, so they should be removed automatically.

## What Changes

- Make icon loading resilient. Any failure to resolve another app's icon results in "no icon" instead of a crash. Icons are resolved using the real package name, not the display name, and are not re-decoded on every recomposition.
- Load the home screen list in pages instead of reading the whole table at once. Keep the per-date grouping, sticky headers, swipe-to-delete and the "go to end" button.
- Stop doing per-item date formatting work on the main thread and inside every Room row construction.
- Capture the notification's icon only when it is a resource icon. Other icon types are stored without an icon instead of crashing the service.
- Add a recurring background routine that deletes notifications older than 2 days. Also hide notifications past that age from the list between routine runs.

## Capabilities

### New Capabilities
- `notification-history`: How captured notifications are listed on the home screen: ordering, date grouping, paging, icon display and deletion.
- `notification-capture`: How posted system notifications are turned into stored entries, including which fields are captured and how unsupported data is handled.
- `notification-retention`: How long notifications are kept and how expired ones are removed automatically.

### Modified Capabilities
<!-- None: no existing specs in openspec/specs/. -->

## Impact

- **Code**: `ContextExt.kt`, `NotificationItemView.kt`, `DismissibleNotificationItemView.kt`, `HomeScreen.kt`, `HomeViewModel.kt`, `NotificationDao.kt`, `NotificationRepository.kt`, `NotificationItem.kt`, `AppNotificationListenerService.kt`, `MyApplication.kt`, `AndroidManifest.xml`.
- **New code**: a cleanup worker and its scheduling.
- **Dependencies**: `androidx.paging` (runtime + compose), `androidx.room:room-paging`, `androidx.work:work-runtime-ktx`, `androidx.hilt:hilt-work` + `androidx.hilt:hilt-compiler`.
- **Data**: no schema change. Existing rows older than 2 days are deleted on the first cleanup run.
