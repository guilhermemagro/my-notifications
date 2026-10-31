# notification-retention Specification

## Purpose

Bounds how long captured notifications are kept, so storage and the home screen list stay small and only recent, relevant notifications are shown.

## Requirements

### Requirement: Notifications older than 2 days are deleted automatically
The system SHALL run a recurring background routine that permanently deletes every notification whose post time is more than 2 days (48 hours) before the time the routine runs. The routine MUST run without the app being open and MUST keep being scheduled across app restarts and device reboots.

#### Scenario: Expired notification is deleted
- **WHEN** the cleanup routine runs and a notification was posted 49 hours earlier
- **THEN** that notification is removed from storage

#### Scenario: Recent notification is kept
- **WHEN** the cleanup routine runs and a notification was posted 47 hours earlier
- **THEN** that notification is kept

#### Scenario: Routine survives reboot
- **WHEN** the device reboots after the app has been launched at least once
- **THEN** the cleanup routine continues to run periodically without the user opening the app

### Requirement: Expired notifications are not shown between cleanup runs
The home screen MUST NOT display notifications older than 2 days, even if the cleanup routine has not run yet.

#### Scenario: Cleanup has not run yet
- **WHEN** a notification became older than 2 days after the last cleanup run and the user opens the home screen
- **THEN** that notification is not displayed

### Requirement: Cleanup is scheduled once
Scheduling the cleanup routine SHALL be idempotent: launching the app repeatedly MUST NOT create duplicate routines or reset the existing schedule.

#### Scenario: App launched several times
- **WHEN** the app is launched multiple times
- **THEN** exactly one periodic cleanup routine is scheduled
