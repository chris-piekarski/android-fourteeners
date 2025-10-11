package com.cpiekarski.fourteeners

import android.app.Application
// Note: Google Analytics imports commented out for now due to build connectivity issues
// import com.google.android.gms.analytics.GoogleAnalytics
// import com.google.android.gms.analytics.Tracker

/**
 * This is a subclass of [Application] used to provide shared objects for this app, such as
 * the Analytics Tracker.
 */
class SummitRegister : Application() {
    // private var mTracker: Tracker? = null

    /**
     * Gets the default Tracker for this [Application].
     * @return tracker
     */
    /*
    @Synchronized
    fun getDefaultTracker(): Tracker {
        if (mTracker == null) {
            val analytics = GoogleAnalytics.getInstance(this)
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker)
            mTracker?.enableAutoActivityTracking(true)
            mTracker?.enableExceptionReporting(true)
        }
        return mTracker!!
    }
    */
    
    // Temporary stub for analytics tracker - will be restored when Google Services are re-enabled
    fun getDefaultTracker(): Any? {
        return null
    }
}