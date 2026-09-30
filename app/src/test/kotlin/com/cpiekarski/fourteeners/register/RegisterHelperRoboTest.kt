package com.cpiekarski.fourteeners.register

import android.content.ContentValues
import com.cpiekarski.fourteeners.SummitRegister
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class RegisterHelperRoboTest {

    private val context by lazy { RuntimeEnvironment.getApplication() as SummitRegister }

    @Before
    fun setUp() {
        val helper = RegisterHelper(context)
        helper.writableDatabase.execSQL("DELETE FROM register")
        helper.writableDatabase.close()
    }

    @Test
    fun `onCreate creates the table`() {
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase
        assertEquals(1, db.query("sqlite_master", null, "type='table' AND name='register'", null, null, null, null).count)
        db.close()
    }

    @Test
    fun `database version is 1`() {
        assertEquals(1, RegisterHelper.DATABASE_VERSION)
    }

    @Test
    fun `database name is summit_register`() {
        assertEquals("summit_register", RegisterHelper.DATABASE_NAME)
    }

    @Test
    fun `onUpgrade drops old data and recreates the table`() {
        val helper = RegisterHelper(context)
        val db = helper.writableDatabase

        // Insert a row to prove onUpgrade actually drops it
        val values = ContentValues().apply {
            put(RegisterHelper.MNT_NAME, "Mt. Elbert")
            put(RegisterHelper.MNT_RANGE, "Sawatch")
            put(RegisterHelper.SUMMIT, 1)
        }
        val rowId = db.insert(RegisterHelper.TABLE_NAME, null, values)
        assertTrue(rowId > 0)
        assertEquals(1, db.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, null).count)

        // onUpgrade should DROP TABLE + recreate, destroying the row
        helper.onUpgrade(db, 1, 2)

        // Table must exist after upgrade (recreate)
        assertEquals(
            1,
            db.query("sqlite_master", null, "type='table' AND name='register'", null, null, null, null).count
        )

        // Old row must be gone (DROP TABLE)
        assertEquals(
            0,
            db.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, null).count
        )

        db.close()
    }
}
