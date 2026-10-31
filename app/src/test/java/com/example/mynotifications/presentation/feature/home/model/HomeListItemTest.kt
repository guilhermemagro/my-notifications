package com.example.mynotifications.presentation.feature.home.model

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.example.mynotifications.data.database.NotificationItem
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class HomeListItemTest {

    private fun millis(day: Int, hour: Int) =
        ZonedDateTime.of(2026, 10, day, hour, 0, 0, 0, ZoneOffset.UTC).toInstant().toEpochMilli()

    private fun item(uid: Int, postTime: Long) = NotificationItem(
        uid = uid,
        title = "t$uid",
        message = "m$uid",
        packageName = "org.example.app",
        postTime = postTime,
        iconRes = null,
    )

    @Test
    fun insertsOneHeaderPerDay_beforeThatDaysNotifications() = runTest {
        val rows = listOf(
            item(1, millis(30, 9)),
            item(2, millis(30, 18)),
            item(3, millis(31, 8)),
        )

        val snapshot = flowOf(PagingData.from(rows))
            .asSnapshotOf { it.toHomeListItems(ZoneOffset.UTC) }

        assertEquals(5, snapshot.size)
        val header30 = snapshot[0] as HomeListItem.DayHeader
        assertTrue(header30.date, header30.date.startsWith("sexta-feira, 30"))
        assertEquals(1, header30.firstUid)
        assertEquals(1, (snapshot[1] as HomeListItem.Notification).model.uid)
        assertEquals(2, (snapshot[2] as HomeListItem.Notification).model.uid)
        val header31 = snapshot[3] as HomeListItem.DayHeader
        assertTrue(header31.date, header31.date.startsWith("sábado, 31"))
        assertEquals(3, header31.firstUid)
        assertEquals(3, (snapshot[4] as HomeListItem.Notification).model.uid)
    }

    @Test
    fun emptyList_hasNoHeaders() = runTest {
        val snapshot = flowOf(PagingData.from(emptyList<NotificationItem>()))
            .asSnapshotOf { it.toHomeListItems(ZoneOffset.UTC) }

        assertTrue(snapshot.isEmpty())
    }

    private suspend fun kotlinx.coroutines.flow.Flow<PagingData<NotificationItem>>.asSnapshotOf(
        transform: (PagingData<NotificationItem>) -> PagingData<HomeListItem>,
    ): List<HomeListItem> = kotlinx.coroutines.flow.flow {
        collect { emit(transform(it)) }
    }.asSnapshot()
}
