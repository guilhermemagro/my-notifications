# Spec Delta

## Purpose

Defines how captured notifications are presented on the home screen: their order, grouping by day, incremental loading, app icon display and user-initiated deletion.

## ADDED Requirements

### Requirement: Home screen opens regardless of history size
The home screen MUST open without crashing, and stay responsive, no matter how many notifications are stored. The system SHALL load the list incrementally and SHALL NOT read the whole history into memory at once.

#### Scenario: Large history
- **WHEN** the database holds thousands of notifications and the user opens the app
- **THEN** the home screen is displayed without a crash and only the notifications needed around the visible area are loaded

#### Scenario: New notification arrives while the screen is open
- **WHEN** a notification is captured while the home screen is visible
- **THEN** the new entry appears in the list without the whole history being reloaded

### Requirement: Notifications are listed chronologically and grouped by day
The system SHALL list notifications from oldest to newest, under a sticky header per calendar day formatted as `EEEE, dd MMM yyyy` in pt-BR. Each entry SHALL show the app name, the time (`HH:mm:ss`), the title and the message.

#### Scenario: Notifications from two days
- **WHEN** the stored notifications were posted on two different days
- **THEN** the list shows two day headers, each followed by that day's notifications in posting order

#### Scenario: Header stays pinned while scrolling
- **WHEN** the user scrolls within a day's notifications
- **THEN** that day's header stays pinned at the top until the next day's header replaces it

### Requirement: Go to end
The system SHALL provide a button that scrolls the list to the most recent notification, even when that part of the list has not been loaded yet.

#### Scenario: Jump to newest
- **WHEN** the user taps the "go to end" button
- **THEN** the list scrolls to the last (most recent) notification

### Requirement: App icon display never crashes the screen
The system SHALL show the originating app's icon when it can be resolved for that app's package. When the icon cannot be resolved for any reason (app uninstalled or updated, resource missing, inaccessible package, unsupported drawable), the entry MUST be shown without an icon instead of failing.

#### Scenario: Icon resource no longer exists
- **WHEN** an entry's stored icon resource ID no longer exists in the originating app (e.g. after an app update)
- **THEN** the entry is rendered without an icon and the app does not crash

#### Scenario: Known app with friendly name
- **WHEN** an entry comes from a package with a friendly display name (e.g. `com.whatsapp` shown as "WhatsApp")
- **THEN** the icon is resolved using the package name, not the display name, and is shown if available

### Requirement: User can delete notifications
The system SHALL let the user delete a single notification by swiping it from end to start, and delete all notifications with a button.

#### Scenario: Swipe to delete
- **WHEN** the user swipes an entry from end to start
- **THEN** that notification is removed from storage and from the list

#### Scenario: Delete all
- **WHEN** the user taps "Delete all notifications"
- **THEN** all stored notifications are removed and the list becomes empty
