package com.example.mynotifications.presentation.icon

import android.content.Context
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import com.example.mynotifications.presentation.extensions.getBitmapFromOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val MAX_CACHED_ICONS = 64

/**
 * Loads other apps' notification icons off the main thread and keeps them in memory, so the
 * same icon is decoded only once (failures are cached too, to avoid retrying on every row).
 */
object AppIconLoader {

    private class CachedIcon(val bitmap: ImageBitmap?)

    private val cache = LruCache<String, CachedIcon>(MAX_CACHED_ICONS)

    private fun key(packageName: String, iconRes: Int) = "$packageName#$iconRes"

    fun getCached(packageName: String, iconRes: Int): ImageBitmap? =
        cache.get(key(packageName, iconRes))?.bitmap

    suspend fun load(context: Context, packageName: String, iconRes: Int): ImageBitmap? {
        val key = key(packageName, iconRes)
        cache.get(key)?.let { return it.bitmap }
        val bitmap = withContext(Dispatchers.IO) {
            context.applicationContext.getBitmapFromOrNull(iconRes, packageName)
        }
        cache.put(key, CachedIcon(bitmap))
        return bitmap
    }
}

/**
 * Returns the icon [iconRes] of [packageName], loaded asynchronously. `null` while loading,
 * when there is no icon, or when it cannot be resolved.
 */
@Composable
fun rememberAppIcon(context: Context?, packageName: String, iconRes: Int?): State<ImageBitmap?> {
    val initial = iconRes?.let { AppIconLoader.getCached(packageName, it) }
    return produceState(initialValue = initial, context, packageName, iconRes) {
        value = if (context != null && iconRes != null && packageName.isNotBlank()) {
            AppIconLoader.load(context, packageName, iconRes)
        } else {
            null
        }
    }
}
