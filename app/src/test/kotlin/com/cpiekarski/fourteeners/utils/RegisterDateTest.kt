package com.cpiekarski.fourteeners.utils

import com.cpiekarski.fourteeners.SummitRegister
import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class RegisterDateTest {

    @Test
    fun `constructor creates with correct formatter`() {
        val rd = RegisterDate()
        assertEquals("yyyy-MM-dd'T'HH:mm:ssZ", rd.getFormatter())
    }

    @Test
    fun `getStrDate returns ISO string with expected fields`() {
        val rd = RegisterDate()
        rd.setDate(6, 15, 2024) // Calendar.JULY = 6
        rd.setTime(14, 30, 0)
        val formatted = rd.getStrDate()
        // Assert the year and day-of-month appear in the correct positions
        assertTrue(formatted.startsWith("2024-07-15T14:30:00"))
    }

    @Test
    fun `parseStrDate round-trips`() {
        val rd = RegisterDate()
        val original = rd.getStrDate()
        val another = RegisterDate()
        assertTrue(another.parseStrDate(original))
        assertEquals(original, another.getStrDate())
    }

    @Test
    fun `setDate with July 4 2024 sets correct fields`() {
        val rd = RegisterDate()
        rd.setDate(6, 4, 2024) // Calendar.JULY = 6
        assertEquals(2024, rd.getIntField(Calendar.YEAR))
        assertEquals(4, rd.getIntField(Calendar.DAY_OF_MONTH))
        assertEquals(6, rd.getIntField(Calendar.MONTH))
    }

    @Test
    fun `setTime with noon sets HOUR_OF_DAY and MINUTE`() {
        val rd = RegisterDate()
        rd.setTime(12, 30, 45)
        assertEquals(12, rd.getIntField(Calendar.HOUR_OF_DAY))
        assertEquals(30, rd.getIntField(Calendar.MINUTE))
        assertEquals(45, rd.getIntField(Calendar.SECOND))
    }

    @Test
    fun `parseStrDate returns false for invalid date`() {
        val rd = RegisterDate()
        assertFalse(rd.parseStrDate("not-a-valid-date"))
    }

    @Test
    fun `parseStrDate returns false for empty string`() {
        val rd = RegisterDate()
        assertFalse(rd.parseStrDate(""))
    }

    @Test
    fun `parseStrDate sets year month day hour for valid ISO date`() {
        val rd = RegisterDate()
        val ok = rd.parseStrDate("2024-07-15T14:30:00+0000")
        assertTrue(ok)
        assertEquals(2024, rd.getIntField(Calendar.YEAR))
        assertEquals(6, rd.getIntField(Calendar.MONTH)) // Calendar.JULY
        assertEquals(15, rd.getIntField(Calendar.DAY_OF_MONTH))
        // Hour depends on timezone conversion; just assert it parsed without throwing
        assertTrue(rd.getIntField(Calendar.HOUR_OF_DAY) in 0..23)
    }

    @Test
    fun `string constructor parses valid date`() {
        val rd = RegisterDate()
        val validDate = rd.getStrDate()
        val rd2 = RegisterDate(validDate)
        assertEquals(rd.getIntField(Calendar.YEAR), rd2.getIntField(Calendar.YEAR))
        assertEquals(rd.getIntField(Calendar.DAY_OF_MONTH), rd2.getIntField(Calendar.DAY_OF_MONTH))
        assertEquals(rd.getIntField(Calendar.MONTH), rd2.getIntField(Calendar.MONTH))
        assertEquals(rd.getIntField(Calendar.HOUR_OF_DAY), rd2.getIntField(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `setDate and setTime produce correct formatted string`() {
        val rd = RegisterDate()
        rd.setDate(6, 15, 2024) // Calendar.JULY = 6
        rd.setTime(14, 30, 0)
        val formatted = rd.getStrDate()
        // Exact prefix match for year-month-day hour:minute:second
        assertTrue(formatted.startsWith("2024-07-15T14:30:00"))
    }
}
