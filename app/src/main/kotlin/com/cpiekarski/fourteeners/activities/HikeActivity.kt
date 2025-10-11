package com.cpiekarski.fourteeners.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.cpiekarski.fourteeners.R
import com.cpiekarski.fourteeners.SummitRegister
import com.cpiekarski.fourteeners.utils.DeviceLocation
import com.cpiekarski.fourteeners.utils.Mountain
import com.cpiekarski.fourteeners.utils.SRLOG

class HikeActivity : Activity() {
    private lateinit var deviceLocation: DeviceLocation
    // private var tracker: Tracker? = null  // Temporarily commented out
    private lateinit var adapter: MyAdapter
    private val tag = "HikeActivity"

    private inner class MyAdapter(
        context: Context,
        resource: Int,
        objects: ArrayList<Mountain>
    ) : ArrayAdapter<Mountain>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val mountain = getItem(position)
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.hike_list_view, parent, false)

            val name = view.findViewById<TextView>(R.id.peak_name)
            val elevation = view.findViewById<TextView>(R.id.peak_elevation_pick)

            name.text = mountain?.getName() ?: ""
            elevation.text = mountain?.getElevation()?.toString() ?: ""
            
            return view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hike)

        val application = application as SummitRegister
        // tracker = application.getDefaultTracker()  // Temporarily commented out

        deviceLocation = DeviceLocation(this)
        deviceLocation.getPassiveUpdates()
    }

    fun manualPick(view: View) {
        SRLOG.v(tag, "Manual pick clicked")
    }

    override fun onResume() {
        super.onResume()
        val mountains = deviceLocation.getNearestMountains(10)

        adapter = MyAdapter(this, R.layout.hike_list_view, mountains)
        val listView = findViewById<ListView>(R.id.nearest_listview)
        listView?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}