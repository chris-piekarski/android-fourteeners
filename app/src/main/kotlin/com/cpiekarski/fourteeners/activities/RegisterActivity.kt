package com.cpiekarski.fourteeners.activities

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.ListView
import com.cpiekarski.fourteeners.R
import com.cpiekarski.fourteeners.SummitRegister
import com.cpiekarski.fourteeners.register.RegisterHelper
import com.cpiekarski.fourteeners.utils.RegisterDate
import com.cpiekarski.fourteeners.utils.SRLOG
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : Activity() {
    private val tag = "RegisterHistoryActivity"
    private lateinit var adapter: SimpleCursorAdapter
    // private var tracker: Tracker? = null  // Temporarily commented out

    private inner class MyAdapter(
        context: Context,
        layout: Int,
        c: Cursor,
        from: Array<String>,
        to: IntArray,
        flags: Int
    ) : SimpleCursorAdapter(context, layout, c, from, to, flags) {
        
        private fun convertDate(text: String): Date? {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
                sdf.parse(text)
            } catch (e: ParseException) {
                null
            }
        }
        
        override fun setViewText(v: TextView, text: String) {
            when (v.id) {
                R.id.peak_summit -> {
                    v.text = if (text == "1") "Summited on:" else "Attempted on:"
                }
                R.id.peak_elevation -> {
                    v.text = "$text Feet"
                }
                R.id.peak_summit_date -> {
                    SRLOG.v(tag, text)
                    val date = RegisterDate(text)
                    val month = date.getIntField(Calendar.MONTH) + 1
                    val day = date.getIntField(Calendar.DAY_OF_MONTH)
                    val year = date.getIntField(Calendar.YEAR)
                    v.text = "$month/$day/$year"
                }
                else -> {
                    v.text = text
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val application = application as SummitRegister
        // tracker = application.getDefaultTracker()  // Temporarily commented out

        val fromCols = arrayOf(
            RegisterHelper.MNT_NAME,
            RegisterHelper.PEEK_ELEVATION,
            RegisterHelper.SUMMIT,
            RegisterHelper.START_TIME
        )
        val toViews = intArrayOf(
            R.id.peak_name,
            R.id.peak_elevation,
            R.id.peak_summit,
            R.id.peak_summit_date
        )

        val r = RegisterHelper(this)
        val d = r.readableDatabase
        val c = d.query(
            RegisterHelper.TABLE_NAME,
            null, null, null, null, null,
            "${RegisterHelper.START_TIME} ASC"
        )

        adapter = MyAdapter(this, R.layout.history_list_view, c, fromCols, toViews, 0)
        
        val listView = findViewById<ListView>(R.id.history_listview)
        listView?.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}