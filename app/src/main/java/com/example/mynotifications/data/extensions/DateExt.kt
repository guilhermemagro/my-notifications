package com.example.mynotifications.data.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FORMAT = "dd/MM/yyyy - HH:mm:ss"
private val defaultLocale = Locale("pt", "BR")

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(DATE_FORMAT, defaultLocale)
    return formatter.format(date)
}
