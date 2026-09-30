package com.cpiekarski.fourteeners.register

import com.cpiekarski.fourteeners.SummitRegister
import com.cpiekarski.fourteeners.utils.Mountain
import com.cpiekarski.fourteeners.utils.Mountains
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class RegisterRoboTest {

    private val context by lazy { RuntimeEnvironment.getApplication() as SummitRegister }
    private lateinit var register: Register

    companion object {
        private val regInstance = Register::class.java.getDeclaredField("INSTANCE").apply { isAccessible = true }
        private val mtInstance = Mountains::class.java.getDeclaredField("INSTANCE").apply { isAccessible = true }

        @JvmStatic
        fun resetSingletons() {
            regInstance.set(null, null)
            mtInstance.set(null, null)
        }
    }

    @Before
    fun setUp() {
        resetSingletons()
        register = Register.getInstance(context)
    }

    @After
    fun tearDown() {
        val helper = RegisterHelper(context)
        helper.writableDatabase.execSQL("DELETE FROM register")
        helper.writableDatabase.close()
    }

    @Test
    fun `getInstance returns singleton`() {
        val second = Register.getInstance(context)
        assertSame(register, second)
    }

    @Test
    fun `NO_VALUE is -1`() {
        assertEquals(-1, Register.NO_VALUE)
    }

    @Test
    fun `getTotalEntries is 0 in a fresh database`() {
        assertEquals(0, register.getTotalEntries())
    }

    @Test
    fun `total summits and unique summits are 0`() {
        assertEquals(0, register.getTotalSummits())
        assertEquals(0, register.getTotalUniqueSummits())
    }

    @Test
    fun `getLastEntry is null when empty`() {
        assertNull(register.getLastEntry())
    }

    @Test
    fun `getTotalEntries increases after inserting Elbert`() {
        assertEquals(0, register.getTotalEntries())

        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(1)
        assertTrue(entry.createEntry())

        assertEquals(1, register.getTotalEntries())
    }

    @Test
    fun `getTotalSummits increases after inserting Elbert summit`() {
        assertEquals(0, register.getTotalSummits())

        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(1)
        assertTrue(entry.createEntry())

        assertEquals(1, register.getTotalSummits())
    }

    @Test
    fun `getTotalSummits for mountain Elbert returns 1`() {
        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(1)
        assertTrue(entry.createEntry())

        assertEquals(1, register.getTotalSummits("Mt. Elbert"))
    }

    @Test
    fun `getLastEntry returns the Elbert entry`() {
        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(1)
        assertTrue(entry.createEntry())

        val last = register.getLastEntry()
        assertNotNull(last)
        assertEquals("Mt. Elbert", last!!.getMountainName())
        assertEquals(1, last.getSummit())
    }

    @Test
    fun `getLastEntry returns most recent summit entry`() {
        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val massip = Mountains.getInstance(context).getMountain("Mt. Massive")!!

        val entry1 = RegisterEntry(context)
        entry1.setMountain(elbert)
        entry1.setSummit(1)
        assertTrue(entry1.createEntry())

        val entry2 = RegisterEntry(context)
        entry2.setMountain(massip)
        entry2.setSummit(1)
        assertTrue(entry2.createEntry())

        assertEquals(2, register.getTotalEntries())
        val last = register.getLastEntry()
        assertNotNull(last)
        assertEquals("Mt. Massive", last!!.getMountainName())
    }

    @Test
    fun `getLastEntry returns null when only attempts exist`() {
        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(0)
        assertTrue(entry.createEntry())

        assertEquals(1, register.getTotalEntries())
        assertNull(register.getLastEntry())
    }
}
