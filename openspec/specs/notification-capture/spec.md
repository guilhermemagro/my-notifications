# notification-capture Specification

## Purpose

Defines how notifications posted on the device are captured and stored by the listener, which fields are kept, and how unsupported or incomplete data is handled without interrupting capture.

## Requirements

### Requirement: Capture non-system notifications with title and text
The system SHALL store a notification when its category is not system and it has a non-blank title, text and package name and a post time. A notification that equals the previously captured one (same title, message, package, icon, and post time within 1 second) SHALL NOT be stored again.

#### Scenario: Regular message notification
- **WHEN** a messaging app posts a notification with a title and text
- **THEN** an entry with title, message, package name, post time and icon (if any) is stored

#### Scenario: Duplicate post
- **WHEN** the same notification is posted twice within 1 second
- **THEN** only one entry is stored

### Requirement: Unsupported icon types do not break capture
The system SHALL store the small icon's resource ID only when the icon is a resource-based icon. For any other icon type (bitmap, URI, adaptive bitmap, data), the entry MUST be stored without an icon, and the listener MUST keep running.

#### Scenario: Bitmap small icon
- **WHEN** an app posts a notification whose small icon is a bitmap icon
- **THEN** the notification is stored without an icon and later notifications continue to be captured
