package com.cpiekarski.fourteeners.register

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cpiekarski.fourteeners.utils.SRLOG

/**
 * Utils class for Register information.
 * O(1)
 */
class Register private constructor(private val context: Context) {
    companion object {
        const val NO_VALUE = -1
        
        @Volatile
        private var INSTANCE: Register? = null
        
        /**
         * Use Register class as a singleton
         */
        @JvmStatic
        fun getInstance(context: Context): Register {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Register(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val tag = "Register"
    private val registerHelper: RegisterHelper
    private val database: SQLiteDatabase
    
    init {
        registerHelper = RegisterHelper(context)
        database = registerHelper.readableDatabase
    }
    
    fun getTotalEntries(): Int {
        val cols = arrayOf(RegisterHelper.MNT_NAME)
        val cursor = database.query(RegisterHelper.TABLE_NAME, cols, null, null, null, null, null)
        val total = cursor.count
        SRLOG.v(tag, "Total entries is: $total")
        cursor.close()
        return total
    }
    
    fun getTotalSummits(): Int {
        val cols = arrayOf(RegisterHelper.MNT_NAME)
        val cursor = database.query(
            RegisterHelper.TABLE_NAME, 
            cols, 
            "${RegisterHelper.SUMMIT} IS 1", 
            null, null, null, null
        )
        val total = cursor.count
        SRLOG.v(tag, "Total summits is: $total")
        cursor.close()
        return total
    }
    
    fun getTotalSummits(mountain: String): Int {
        val cols = arrayOf(RegisterHelper.MNT_NAME)
        val cursor = database.query(
            RegisterHelper.TABLE_NAME, 
            cols,
            "${RegisterHelper.SUMMIT} IS 1 AND ${RegisterHelper.MNT_NAME} IS ?",
            arrayOf(mountain), 
            null, null, null
        )
        val total = cursor.count
        SRLOG.v(tag, "Total summits for $mountain is: $total")
        cursor.close()
        return total
    }
    
    fun getTotalUniqueSummits(): Int {
        val cols = arrayOf(RegisterHelper.MNT_NAME)
        // distinct query
        val cursor = database.query(
            true, 
            RegisterHelper.TABLE_NAME, 
            cols, 
            "${RegisterHelper.SUMMIT} IS 1", 
            null, null, null, null, null
        )
        val total = cursor.count
        SRLOG.v(tag, "Total unique summits is: $total")
        cursor.close()
        return total
    }
    
    /**
     * Gets the last entry in the register with a successful summit.
     * @return Entry or null
     */
    fun getLastEntry(): RegisterEntry? {
        val cols = arrayOf("_id")
        val cursor = database.query(
            RegisterHelper.TABLE_NAME, 
            cols,
            "${RegisterHelper.SUMMIT} IS 1", 
            null, null, null, "_id DESC", "1"
        )
        
        if (cursor.count == 0) {
            cursor.close()
            return null
        }
        
        cursor.moveToNext()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
        val registerEntry = RegisterEntry(context, id)
        cursor.close()
        return registerEntry
    }
}