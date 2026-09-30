package com.cpiekarski.fourteeners

import com.cpiekarski.fourteeners.utils.Mountain
import com.cpiekarski.fourteeners.utils.Mountains
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class MountainsTest {

    private lateinit var mountains: Mountains
    private val context by lazy { RuntimeEnvironment.getApplication() as SummitRegister }

    companion object {
        private val instanceField = Mountains::class.java.getDeclaredField("INSTANCE")
            .apply { isAccessible = true }

        @JvmStatic
        fun resetSingleton() {
            instanceField.set(null, null)
        }
    }

    @Before
    fun setUp() {
        resetSingleton()
        mountains = Mountains.getInstance(context)
    }

    @Test
    fun `getSize is 53`() {
        assertEquals(53, mountains.getSize())
    }

    @Test
    fun `rank -1 mountains excluded from getAllPeakNames`() {
        val names = mountains.getAllPeakNames()
        assertFalse("Mt. Cameron (rank=-1) must not appear in peak names",
            names.any { it == "Mt. Cameron" })
    }

    @Test
    fun `rank -1 mountains excluded from getNamesInRange`() {
        // Mt. Cameron is rank=-1 in the Mosquito range
        val mosquitoNames = mountains.getNamesInRange("Mosquito")
        assertNotNull(mosquitoNames)
        assertFalse("Mt. Cameron must not appear in Mosquito range names",
            mosquitoNames!!.any { it == "Mt. Cameron" })
    }

    @Test
    fun `getMountain MtElbert has correct elevation range county`() {
        val elbert = mountains.getMountain("Mt. Elbert")
        assertNotNull(elbert)
        assertEquals(14433, elbert!!.getElevation())
        assertEquals("Sawatch", elbert.getRange())
        assertEquals("Lake", elbert.getCounty())
    }

    @Test
    fun `getMountain by name and range returns Elbert`() {
        val elbert = mountains.getMountain("Mt. Elbert", "Sawatch")
        assertNotNull(elbert)
        assertEquals("Mt. Elbert", elbert!!.getName())
    }

    @Test
    fun `getNamesInRange UnknownRange returns null`() {
        assertNull(mountains.getNamesInRange("UnknownRange"))
    }

    @Test
    fun `getMountain DoesNotExist returns null`() {
        assertNull(mountains.getMountain("DoesNotExist"))
    }

    @Test
    fun `getInstance is a singleton`() {
        val second = Mountains.getInstance(context)
        assertSame(mountains, second)
    }

    @Test
    fun `getRanges returns non-empty array containing Sawatch`() {
        val ranges = mountains.getRanges()
        assertTrue(ranges.isNotEmpty())
        assertTrue(ranges.any { it == "Sawatch" })
        assertTrue(ranges.any { it == "Elk" })
    }
}
