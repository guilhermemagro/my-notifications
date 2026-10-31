package com.example.mynotifications.data.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val defaultLocale = Locale.forLanguageTag("pt-BR")

// DateTimeFormatter is immutable and thread-safe, so it is created once and reused.
private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", defaultLocale)
private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", defaultLocale)

/**
 * Returns a String with date in format EEEE, dd MMM yyyy.
 */
fun Long.toFormattedDate(zone: ZoneId = ZoneId.systemDefault()): String =
    dateFormatter.format(Instant.ofEpochMilli(this).atZone(zone))

/**
 * Returns a String with hour in format HH:mm:ss.
 */
fun Long.toFormattedHour(zone: ZoneId = ZoneId.systemDefault()): String =
    hourFormatter.format(Instant.ofEpochMilli(this).atZone(zone))
