package com.cpiekarski.fourteeners.utils

import android.content.Context
import android.content.res.XmlResourceParser
import com.cpiekarski.fourteeners.R
import com.cpiekarski.fourteeners.SummitRegister
import java.util.*

/**
 * Maintains a TreeMap for known peaks.
 */
class Mountains private constructor(private val context: Context) {
    private val tag = "Mountains"
    private val mountains = TreeMap<String, Mountain>()
    private val ranges = TreeMap<String, ArrayList<String>>()
    private val tracker: Any? // Temporarily Any? instead of Tracker

    companion object {
        @Volatile
        private var INSTANCE: Mountains? = null

        /**
         * Use Mountains class as a singleton
         */
        @JvmStatic
        fun getInstance(context: Context): Mountains {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Mountains(context.applicationContext).also { 
                    INSTANCE = it
                    it.parseFourteeners()
                }
            }
        }
    }

    init {
        val application = context.applicationContext as SummitRegister
        tracker = application.getDefaultTracker()
    }

    fun getRanges(): Array<String> {
        return ranges.keys.toTypedArray()
    }

    fun getNamesInRange(range: String): Array<String>? {
        return ranges[range]?.toTypedArray()
    }

    fun getAllPeakNames(): Array<String> {
        return mountains.keys.toTypedArray()
    }

    /**
     * O(C)
     * @return the number of peaks in the list
     */
    fun getSize(): Int {
        return mountains.size
    }

    /**
     * Get a known mountain by name only.
     */
    fun getMountain(name: String): Mountain? {
        return mountains[name]
    }

    /**
     * Get a known mountain by name and range.
     * O(log n)
     *
     * @param name of the Mountain
     * @param range the Mountain is in
     * @return The correct mountain object
     */
    fun getMountain(name: String, range: String): Mountain? {
        // TODO: add range check
        return mountains[name]
    }

    /**
     * O(n log n) add and sort by name
     */
    private fun parseFourteeners() {
        SRLOG.v(tag, "Parsing fourteener data...")
        val res = context.resources
        val xrp: XmlResourceParser = res.getXml(R.xml.mountain_data)
        
        try {
            xrp.next() // skip first 'mountains' element
            while (xrp.eventType != XmlResourceParser.END_DOCUMENT) {
                xrp.next() // get first 'mountain' element
                if (xrp.eventType == XmlResourceParser.START_TAG) {
                    // double check it's the right element
                    if (xrp.name == "mountain") {
                        // extract the data you want
                        val count = xrp.attributeCount
                        val name = xrp.getAttributeValue(null, "name")
                        val rank = xrp.getAttributeValue(null, "rank")
                        val elev = xrp.getAttributeValue(null, "elevation")
                        val range = xrp.getAttributeValue(null, "range")
                        val longitude = xrp.getAttributeValue(null, "long")
                        val latitude = xrp.getAttributeValue(null, "lat")
                        val county = xrp.getAttributeValue(null, "county")

                        // Check if all required attributes are present
                        if (name != null && rank != null && elev != null && range != null && 
                            longitude != null && latitude != null && county != null) {
                            
                            val mountain = Mountain(
                                name = name,
                                range = range,
                                county = county,
                                longitude = longitude.toDouble(),
                                latitude = latitude.toDouble(),
                                rank = rank.toInt(),
                                elevation = elev.toInt()
                            )
                            
                            // Only include official fourteeners (rank > 0)
                            if (rank.toInt() > 0) {
                                mountains[name] = mountain
                            }

                            // Only add to ranges if it's an official fourteener
                            if (rank.toInt() > 0) {
                                if (ranges.containsKey(range)) {
                                    ranges[range]?.add(name)
                                } else {
                                    val newList = ArrayList<String>()
                                    newList.add(name)
                                    ranges[range] = newList
                                }
                            }
                        } else {
                            SRLOG.w(tag, "Skipping mountain with missing attributes: name=$name, rank=$rank, elev=$elev, range=$range, longitude=$longitude, latitude=$latitude, county=$county")
                        }

                        SRLOG.v(tag, "Mountain Attribute Count $count")
                        SRLOG.v(tag, "Peak Name $name")
                        SRLOG.v(tag, "Peak Elevation $elev")
                    }
                }
            }
        } catch (e: Exception) {
            SRLOG.e(tag, e.toString())
            // Analytics tracking temporarily commented out
            /*
            tracker?.send(
                HitBuilders.ExceptionBuilder()
                    .setDescription(
                        StandardExceptionParser(context, null)
                            .getDescription(Thread.currentThread().name, e)
                    )
                    .setFatal(false)
                    .build()
            )
            */
        } finally {
            xrp.close()
            SRLOG.i(tag, "Fourteener data parsed")
        }
    }
}