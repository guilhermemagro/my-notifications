package com.example.mynotifications.data.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FORMAT = "dd/MM/yyyy"
private const val HOUR_FORMAT = "HH:mm:ss"
private val defaultLocale = Locale("pt", "BR")

/**
 * Returns a String with date in format dd/MM/yyyy.
 */
fun Long.toFormattedDate(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(DATE_FORMAT, defaultLocale)
    return formatter.format(date)
}

/**
 * Returns a String with hour in format HH:mm:ss.
 */
fun Long.toFormattedHour(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(HOUR_FORMAT, defaultLocale)
    return formatter.format(date)
}
