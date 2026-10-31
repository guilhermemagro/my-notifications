package com.example.mynotifications.presentation.extensions

import android.content.Context
import android.content.Context.CONTEXT_IGNORE_SECURITY
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.example.mynotifications.data.utilities.LOG_TAG

/**
 * Loads the drawable [id] from the package [packageName] as an [ImageBitmap].
 *
 * Never throws: any failure (package not found or not visible, resource missing after an
 * app update, unsupported drawable, etc.) results in `null`.
 */
fun Context.getBitmapFromOrNull(id: Int, packageName: String): ImageBitmap? {
    return try {
        createPackageContext(packageName, CONTEXT_IGNORE_SECURITY)
            ?.getDrawable(id)
            ?.toBitmapOrNull()
            ?.asImageBitmap()
    } catch (e: Exception) {
        Log.w(LOG_TAG, "Could not load icon $id from $packageName", e)
        null
    }
}
