package com.example.mynotifications.presentation.extensions

import android.content.Context
import android.content.Context.CONTEXT_IGNORE_SECURITY
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmapOrNull

fun Context.getBitmapFromOrNull(id: Int, packageName: String): ImageBitmap? {
    return try {
        createPackageContext(packageName, CONTEXT_IGNORE_SECURITY)
            ?.getDrawable(id)
            ?.toBitmapOrNull()
            ?.asImageBitmap()
    } catch (e: NameNotFoundException) {
        null
    }
}
