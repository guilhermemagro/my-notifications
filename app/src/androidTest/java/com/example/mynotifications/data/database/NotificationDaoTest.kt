package com.example.mynotifications.data.database

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.hours

@RunWith(AndroidJUnit4::class)
class NotificationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: NotificationDao

    private val now = System.currentTimeMillis()
    private fun hoursAgo(hours: Int) = now - hours.hours.inWholeMilliseconds
    private val cutoff = hoursAgo(48)

    private fun notification(title: String, postTime: Long) = NotificationItem(
        title = title,
        message = "message",
        packageName = "org.example.app",
        postTime = postTime,
        iconRes = null,
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.notificationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun pagingSource_excludesRowsOlderThanCutoff_andOrdersAscending() = runTest {
        dao.insert(notification("old", hoursAgo(49)))
        dao.insert(notification("recent-1", hoursAgo(47)))
        dao.insert(notification("recent-2", hoursAgo(1)))

        val pager = TestPager(PagingConfig(pageSize = 10), dao.pagingSource(cutoff))
        val page = pager.refresh() as PagingSource.LoadResult.Page

        assertEquals(listOf("recent-1", "recent-2"), page.data.map { it.title })
    }

    @Test
    fun deleteOlderThan_deletesOnlyExpiredRows() = runTest {
        dao.insert(notification("old", hoursAgo(49)))
        dao.insert(notification("recent", hoursAgo(47)))

        val deleted = dao.deleteOlderThan(cutoff)

        val pager = TestPager(PagingConfig(pageSize = 10), dao.pagingSource(0L))
        val page = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(1, deleted)
        assertEquals(listOf("recent"), page.data.map { it.title })
    }

    @Test
    fun deleteById_removesOnlyThatRow() = runTest {
        dao.insert(notification("a", hoursAgo(1)))
        dao.insert(notification("b", hoursAgo(1)))
        val pager = TestPager(PagingConfig(pageSize = 10), dao.pagingSource(0L))
        val first = (pager.refresh() as PagingSource.LoadResult.Page).data.first()

        dao.deleteById(first.uid)

        val after = TestPager(PagingConfig(pageSize = 10), dao.pagingSource(0L))
        assertEquals(listOf("b"), (after.refresh() as PagingSource.LoadResult.Page).data.map { it.title })
    }
}
