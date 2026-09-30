package com.cpiekarski.fourteeners.utils

import android.util.Log
import com.cpiekarski.fourteeners.SummitRegister
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class SRLOGTest {

    companion object {
        private val silentField = SRLOG::class.java.getDeclaredField("silent").apply { isAccessible = true }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `v forwards to Logv in debug mode`() {
        mockkStatic(Log::class)
        every { Log.v(any<String>(), any<String>()) } returns 0

        SRLOG.v("TestTag", "test message")

        verify { Log.v("TestTag", "test message") }
    }

    @Test
    fun `d forwards to Logd in debug mode`() {
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0

        SRLOG.d("TestTag", "test message")

        verify { Log.d("TestTag", "test message") }
    }

    @Test
    fun `i forwards to Logi`() {
        mockkStatic(Log::class)
        every { Log.i(any<String>(), any<String>()) } returns 0

        SRLOG.i("TestTag", "info msg")

        verify { Log.i("TestTag", "info msg") }
    }

    @Test
    fun `w forwards to Logw`() {
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>()) } returns 0

        SRLOG.w("TestTag", "warn msg")

        verify { Log.w("TestTag", "warn msg") }
    }

    @Test
    fun `e forwards to Loge`() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0

        SRLOG.e("TestTag", "error msg")

        verify { Log.e("TestTag", "error msg") }
    }

    @Test
    fun `wtf forwards to Logwtf`() {
        mockkStatic(Log::class)
        every { Log.wtf(any<String>(), any<String>()) } returns 0

        SRLOG.wtf("TestTag", "wtf msg")

        verify { Log.wtf("TestTag", "wtf msg") }
    }

    @Test
    fun `v and d do not call Log when in release mode`() {
        try {
            silentField.setBoolean(null, false) // simulate release build

            mockkStatic(Log::class)
            every { Log.v(any<String>(), any<String>()) } returns 0
            every { Log.d(any<String>(), any<String>()) } returns 0

            SRLOG.v("Tag", "verbose")
            SRLOG.d("Tag", "debug")

            // In release mode, v and d must NOT forward to Log
            verify(exactly = 0) { Log.v("Tag", "verbose") }
            verify(exactly = 0) { Log.d("Tag", "debug") }
        } finally {
            silentField.setBoolean(null, true)
        }
    }
}
