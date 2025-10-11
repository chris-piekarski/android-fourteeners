package com.cpiekarski.fourteeners.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.cpiekarski.fourteeners.R
import com.cpiekarski.fourteeners.SummitRegister

/**
 * A PreferenceActivity that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 */
class SettingsActivity : PreferenceActivity() {
    // private var tracker: Tracker? = null  // Temporarily commented out

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val application = application as SummitRegister
        // tracker = application.getDefaultTracker()  // Temporarily commented out
        
        // For now, just use simple preferences
        addPreferencesFromResource(R.xml.pref_general)
    }

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this) && !isSimplePreferences(this)
    }

    /**
     * Helper method to determine if the device has an extra-large screen.
     */
    private fun isXLargeTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and 
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
    }

    /**
     * Determines whether the simplified settings UI should be shown.
     */
    private fun isSimplePreferences(context: Context): Boolean {
        return true // Simplified for migration
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName ||
               GeneralPreferenceFragment::class.java.name == fragmentName ||
               ICEPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only.
     */
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
        }
    }

    /**
     * This fragment shows ICE (In Case of Emergency) preferences.
     */
    class ICEPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_ice)
        }
    }
}