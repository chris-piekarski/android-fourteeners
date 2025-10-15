package com.cpiekarski.fourteeners.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cpiekarski.fourteeners.R
import com.cpiekarski.fourteeners.SummitRegister
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AddBagActivity : Activity(), AdapterView.OnItemSelectedListener {
    // private var tracker: Tracker? = null  // Temporarily commented out

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bag)

        val application = application as SummitRegister
        // tracker = application.getDefaultTracker()  // Temporarily commented out
    }

    fun cancelAddPeak(view: View) {
        finish()
    }

    fun addAddPeak(view: View) {
        // TODO: Implement add-peak saving logic
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Handle item selection
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle case where nothing is selected
    }

    fun showStartTimePickerDialog(view: View) {
        // TODO: Implement time picker dialog
    }

    fun showEndTimePickerDialog(view: View) {
        // TODO: Implement time picker dialog
    }

    fun showDatePickerDialog(view: View) {
        // TODO: Implement date picker dialog
    }

    @Throws(IOException::class)
    fun copy(src: File, dst: File) {
        FileInputStream(src).use { inStream ->
            FileOutputStream(dst).use { outStream ->
                val inChannel = inStream.channel
                val outChannel = outStream.channel
                inChannel.transferTo(0, inChannel.size(), outChannel)
            }
        }
    }

    fun sendGetProofIntent(view: View) {
        // TODO: Implement photo capture intent
    }

    fun getPath(uri: Uri): String? {
        // TODO: Implement URI to path conversion
        return null
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}