package com.cpiekarski.fourteeners.register

import android.content.ContentValues
import com.cpiekarski.fourteeners.SummitRegister
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
class RegisterEntryRoboTest {

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
    fun `NO_VALUE is -1`() {
        assertEquals(-1, RegisterEntry.NO_VALUE)
    }

    @Test
    fun `constructor without id does not readEntry`() {
        val entry = RegisterEntry(context)
        assertEquals(RegisterEntry.NO_VALUE, entry.getStartElevation())
        assertEquals(RegisterEntry.NO_VALUE, entry.getEndElevation())
        assertEquals(RegisterEntry.NO_VALUE, entry.getPeekElevation())
        assertNull(entry.getMountainName())
        assertNull(entry.getMountainRange())
        assertNull(entry.getDistance())
    }

    @Test
    fun `round-trip Elbert summit preserves all fields`() {
        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(1)
        entry.setStartTime("2024-07-01T08:00:00+0000")
        entry.setEndTime("2024-07-01T18:00:00+0000")
        entry.setNotes("Great climb")
        entry.setProof("photo.jpg")
        entry.setDistance("15 miles")
        assertTrue(entry.createEntry())

        val read = RegisterEntry(context, entry.getRowId().toInt())
        assertEquals("Mt. Elbert", read.getMountainName())
        assertEquals("Sawatch", read.getMountainRange())
        assertEquals(1, read.getSummit())
        assertEquals("2024-07-01T08:00:00+0000", read.getStartTime())
        assertEquals("2024-07-01T18:00:00+0000", read.getEndTime())
        assertEquals("Great climb", read.getNotes())
        assertEquals("photo.jpg", read.getProof())
        assertEquals("15 miles", read.getDistance())
        assertEquals(1, register.getTotalEntries())
    }

    @Test
    fun `createEntry round-trip with location tuple stores and reads coordinates`() {
        val elbert = Mountains.getInstance(context).getMountain("Mt. Elbert")!!
        val entry = RegisterEntry(context)
        entry.setMountain(elbert)
        entry.setSummit(1)

        // No public setters for lat/lon/accuracy — use reflection to set private fields
        setPrivateDouble(entry, "startLatLoc", 39.118)
        setPrivateDouble(entry, "startLongLoc", -106.445)
        setPrivateDouble(entry, "endLatLoc", 39.200)
        setPrivateDouble(entry, "endLongLoc", -106.500)
        setPrivateField(entry, "startLocAccuracy", 100)
        setPrivateField(entry, "endLocAccuracy", 200)

        assertTrue(entry.createEntry())
        val rowId = entry.getRowId().toInt()
        assertTrue(rowId > 0)

        // Read back via separate instance — proves the tuple was written by createEntry
        // and parsed correctly by readEntry
        val read = RegisterEntry(context, rowId)
        assertEquals(39.118, read.getStartLatitude(), 0.001)
        assertEquals(-106.445, read.getStartLongitude(), 0.001)
        assertEquals(100, read.getStartAccuracy())
        assertEquals(39.200, read.getEndLatitude(), 0.001)
        assertEquals(-106.500, read.getEndLongitude(), 0.001)
        assertEquals(200, read.getEndAccuracy())
    }

    @Test
    fun `NULL summit column reads as NO_VALUE`() {
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(RegisterHelper.MNT_NAME, "Mt. Elbert")
            put(RegisterHelper.MNT_RANGE, "Sawatch")
            putNull(RegisterHelper.SUMMIT)
        }
        val rowId = db.insert(RegisterHelper.TABLE_NAME, null, values)

        val entry = RegisterEntry(context, rowId.toInt())
        assertEquals(RegisterEntry.NO_VALUE, entry.getSummit())
    }

    @Test
    fun `NULL DB columns read as NO_VALUE for elevations`() {
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(RegisterHelper.MNT_NAME, "Mt. Elbert")
            put(RegisterHelper.MNT_RANGE, "Sawatch")
            putNull(RegisterHelper.SUMMIT)
            putNull(RegisterHelper.START_ELEVATION)
            putNull(RegisterHelper.END_ELEVATION)
            putNull(RegisterHelper.PEEK_ELEVATION)
            putNull(RegisterHelper.START_LOC)
            putNull(RegisterHelper.END_LOC)
            putNull(RegisterHelper.DISTANCE)
        }
        val rowId = db.insert(RegisterHelper.TABLE_NAME, null, values)

        val entry = RegisterEntry(context, rowId.toInt())
        assertEquals(RegisterEntry.NO_VALUE, entry.getSummit())
        assertEquals(RegisterEntry.NO_VALUE, entry.getStartElevation())
        assertEquals(RegisterEntry.NO_VALUE, entry.getEndElevation())
        assertEquals(RegisterEntry.NO_VALUE, entry.getPeekElevation())
        assertNull(entry.getDistance())
    }

    @Test
    fun `readEntry with nonexistent id leaves fields at defaults`() {
        val entry = RegisterEntry(context, 999999)
        assertEquals(RegisterEntry.NO_VALUE, entry.getStartElevation())
        assertEquals(RegisterEntry.NO_VALUE, entry.getSummit())
    }

    @Test
    fun `createEntry returns false without mountain`() {
        val entry = RegisterEntry(context)
        assertFalse(entry.createEntry())
    }

    @Test
    fun `getter round-trip preserves values`() {
        val entry = RegisterEntry(context)
        entry.setStartTime("08:00:00")
        entry.setEndTime("18:00:00")
        entry.setSummit(1)
        entry.setNotes("test notes")
        entry.setDistance("15 km")
        entry.setProof("receipt")

        assertEquals("08:00:00", entry.getStartTime())
        assertEquals("18:00:00", entry.getEndTime())
        assertEquals(1, entry.getSummit())
        assertEquals("test notes", entry.getNotes())
        assertEquals("15 km", entry.getDistance())
        assertEquals("receipt", entry.getProof())
    }

    @Test
    fun `raw insert location tuple parsers work correctly`() {
        // Direct DB insert validates raw tuple parsing in readEntry
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(RegisterHelper.MNT_NAME, "Mt. Elbert")
            put(RegisterHelper.MNT_RANGE, "Sawatch")
            put(RegisterHelper.SUMMIT, 1)
            put(RegisterHelper.START_LOC, "39.118;-106.445;100")
            put(RegisterHelper.END_LOC, "39.200;-106.500;200")
        }
        val rowId = db.insert(RegisterHelper.TABLE_NAME, null, values)

        val read = RegisterEntry(context, rowId.toInt())
        assertEquals(39.118, read.getStartLatitude(), 0.001)
        assertEquals(-106.445, read.getStartLongitude(), 0.001)
        assertEquals(100, read.getStartAccuracy())
        assertEquals(39.200, read.getEndLatitude(), 0.001)
        assertEquals(-106.500, read.getEndLongitude(), 0.001)
        assertEquals(200, read.getEndAccuracy())
    }

    @Test
    fun `bad tuple format leaves all fields unset`() {
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(RegisterHelper.MNT_NAME, "Mt. Elbert")
            put(RegisterHelper.MNT_RANGE, "Sawatch")
            put(RegisterHelper.START_LOC, "bad_location_no_semicolons")
            put(RegisterHelper.END_LOC, "also_bad")
        }
        val rowId = db.insert(RegisterHelper.TABLE_NAME, null, values)
        val entry = RegisterEntry(context, rowId.toInt())
        assertEquals(-1.0, entry.getStartLatitude(), 0.001)
        assertEquals(-1.0, entry.getStartLongitude(), 0.001)
        assertEquals(-1, entry.getStartAccuracy())
        assertEquals(-1.0, entry.getEndLatitude(), 0.001)
        assertEquals(-1.0, entry.getEndLongitude(), 0.001)
        assertEquals(-1, entry.getEndAccuracy())
    }

    @Test
    fun `NumberFormatException in tuple leaves all fields unset`() {
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(RegisterHelper.MNT_NAME, "Mt. Elbert")
            put(RegisterHelper.MNT_RANGE, "Sawatch")
            put(RegisterHelper.START_LOC, "39.1;nope;1")
            put(RegisterHelper.END_LOC, "40.0;bad;2")
        }
        val rowId = db.insert(RegisterHelper.TABLE_NAME, null, values)
        val entry = RegisterEntry(context, rowId.toInt())
        assertEquals(-1.0, entry.getStartLatitude(), 0.001)
        assertEquals(-1.0, entry.getStartLongitude(), 0.001)
        assertEquals(-1, entry.getStartAccuracy())
        assertEquals(-1.0, entry.getEndLatitude(), 0.001)
        assertEquals(-1.0, entry.getEndLongitude(), 0.001)
        assertEquals(-1, entry.getEndAccuracy())
    }

    @Test
    fun `null mountain leaves name and range null`() {
        val entry = RegisterEntry(context)
        assertNull(entry.getMountainName())
        assertNull(entry.getMountainRange())
    }

    /** Set a Double private field via reflection */
    private fun setPrivateDouble(target: Any, fieldName: String, value: Double) {
        val f = target::class.java.getDeclaredField(fieldName).apply { isAccessible = true }
        f.setDouble(target, value)
    }

    /** Set an Int private field via reflection */
    private fun setPrivateField(target: Any, fieldName: String, value: Int) {
        val f = target::class.java.getDeclaredField(fieldName).apply { isAccessible = true }
        f.setInt(target, value)
    }
}
