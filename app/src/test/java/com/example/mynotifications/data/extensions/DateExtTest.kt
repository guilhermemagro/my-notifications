package com.example.mynotifications.data.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DateExtTest {

    // Sexta-feira, 30 out 2026, 14:05:09 UTC
    private val millis = ZonedDateTime.of(2026, 10, 30, 14, 5, 9, 0, ZoneOffset.UTC)
        .toInstant().toEpochMilli()

    @Test
    fun toFormattedHour_usesHHmmss() {
        assertEquals("14:05:09", millis.toFormattedHour(ZoneOffset.UTC))
    }

    @Test
    fun toFormattedHour_respectsZone() {
        assertEquals("11:05:09", millis.toFormattedHour(ZoneOffset.ofHours(-3)))
    }

    @Test
    fun toFormattedDate_usesPtBrWeekdayDayMonthYear() {
        val formatted = millis.toFormattedDate(ZoneOffset.UTC)
        // The month abbreviation may or may not end with "." depending on the locale data.
        assertTrue(formatted, formatted.startsWith("sexta-feira, 30 out"))
        assertTrue(formatted, formatted.endsWith(" 2026"))
    }
}
