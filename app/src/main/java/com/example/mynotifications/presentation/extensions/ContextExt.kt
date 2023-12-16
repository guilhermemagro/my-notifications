package com.example.mynotifications.presentation.extensions

import android.content.Context
import android.content.Context.CONTEXT_IGNORE_SECURITY
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmapOrNull

fun Context.getBitmapFromOrNull(id: Int, packageName: String): ImageBitmap? {
    return createPackageContext(packageName, CONTEXT_IGNORE_SECURITY)
        ?.getDrawable(id)
        ?.toBitmapOrNull()
        ?.asImageBitmap()
}
