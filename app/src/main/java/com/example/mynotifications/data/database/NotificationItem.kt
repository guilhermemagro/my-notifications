package com.example.mynotifications.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val title: String,
    val message: String,
) {
    constructor(title: String, message: String) : this(0, title, message)
}
