package com.cpiekarski.fourteeners.register

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RegisterHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "summit_register"
        const val COLUMN_ID = "_id"
        const val TABLE_NAME = "register"
        const val MNT_NAME = "mnt_name"
        const val MNT_RANGE = "mnt_range"
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val START_ELEVATION = "start_elevation"
        const val END_ELEVATION = "end_elevation"
        const val PEEK_ELEVATION = "peek_elevation"
        const val DISTANCE = "distance"
        const val START_LOC = "start_location"
        const val END_LOC = "end_location"
        const val NOTES = "notes"
        const val PROOF = "proof"
        const val SUMMIT = "summit"

        private val TABLE_CREATE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $MNT_NAME TEXT,
                $MNT_RANGE TEXT,
                $START_TIME TEXT,
                $END_TIME TEXT,
                $START_ELEVATION INTEGER,
                $END_ELEVATION INTEGER,
                $PEEK_ELEVATION INTEGER,
                $START_LOC TEXT,
                $END_LOC TEXT,
                $DISTANCE TEXT,
                $SUMMIT INTEGER,
                $NOTES TEXT,
                $PROOF TEXT
            );
        """.trimIndent()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO: Implement upgrade logic
    }
}