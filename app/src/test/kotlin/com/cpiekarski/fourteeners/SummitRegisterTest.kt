package com.cpiekarski.fourteeners

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class SummitRegisterTest {

    @Test
    fun `getDefaultTracker returns null`() {
        val app = RuntimeEnvironment.getApplication() as SummitRegister
        assertNull("Tracker is null since analytics is disabled", app.getDefaultTracker())
    }

    @Test
    fun `Application is SummitRegister`() {
        val app = RuntimeEnvironment.getApplication()
        assertTrue("Application should be SummitRegister instance",
            app is SummitRegister)
    }
}
