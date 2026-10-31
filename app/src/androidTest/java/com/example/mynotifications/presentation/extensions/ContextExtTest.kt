package com.example.mynotifications.presentation.extensions

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

private const val MISSING_RESOURCE_ID = 0x7f999999

@RunWith(AndroidJUnit4::class)
class ContextExtTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun missingResourceInExistingPackage_returnsNull() {
        assertNull(context.getBitmapFromOrNull(MISSING_RESOURCE_ID, context.packageName))
    }

    @Test
    fun unknownPackage_returnsNull() {
        assertNull(context.getBitmapFromOrNull(MISSING_RESOURCE_ID, "com.example.does.not.exist"))
    }

    @Test
    fun displayNameInsteadOfPackage_returnsNull() {
        assertNull(context.getBitmapFromOrNull(MISSING_RESOURCE_ID, "WhatsApp"))
    }
}
