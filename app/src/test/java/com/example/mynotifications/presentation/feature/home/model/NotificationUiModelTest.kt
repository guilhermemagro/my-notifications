package com.example.mynotifications.presentation.feature.home.model

import com.example.mynotifications.data.database.NotificationItem
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneOffset

class NotificationUiModelTest {

    private fun item(packageName: String) = NotificationItem(
        uid = 7,
        title = "Title",
        message = "Message",
        packageName = packageName,
        postTime = 0L,
        iconRes = 42,
    )

    @Test
    fun knownPackage_mapsToFriendlyName_andKeepsPackageName() {
        val model = item("com.whatsapp").toUiModel(ZoneOffset.UTC)
        assertEquals("WhatsApp", model.appName)
        assertEquals("com.whatsapp", model.packageName)
    }

    @Test
    fun unknownPackage_mapsToItself() {
        val model = item("org.example.app").toUiModel(ZoneOffset.UTC)
        assertEquals("org.example.app", model.appName)
    }

    @Test
    fun copiesFieldsAndFormatsHour() {
        val model = item("org.example.app").toUiModel(ZoneOffset.UTC)
        assertEquals(7, model.uid)
        assertEquals("Title", model.title)
        assertEquals("Message", model.message)
        assertEquals(42, model.iconRes)
        assertEquals("00:00:00", model.formattedHour)
    }
}
