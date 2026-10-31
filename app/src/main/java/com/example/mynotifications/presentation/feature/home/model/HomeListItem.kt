package com.example.mynotifications.presentation.feature.home.model

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.mynotifications.data.database.NotificationItem
import java.time.ZoneId

sealed interface HomeListItem {
    /** Day header shown before the first notification ([firstUid]) of each day. */
    data class DayHeader(val date: String, val firstUid: Int) : HomeListItem {
        val key: String get() = "header_$firstUid"
    }

    data class Notification(val model: NotificationUiModel) : HomeListItem
}

/** Maps rows to UI models and inserts a [HomeListItem.DayHeader] whenever the day changes. */
fun PagingData<NotificationItem>.toHomeListItems(
    zone: ZoneId = ZoneId.systemDefault(),
): PagingData<HomeListItem> =
    map { HomeListItem.Notification(it.toUiModel(zone)) }
        .insertSeparators { before: HomeListItem.Notification?, after: HomeListItem.Notification? ->
            when {
                after == null -> null
                before == null || before.model.formattedDate != after.model.formattedDate ->
                    HomeListItem.DayHeader(after.model.formattedDate, after.model.uid)

                else -> null
            }
        }
