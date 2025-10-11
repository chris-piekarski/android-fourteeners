package com.cpiekarski.fourteeners.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.cpiekarski.fourteeners.R
import com.cpiekarski.fourteeners.SummitRegister
import com.cpiekarski.fourteeners.register.Register

class HomeActivity : Activity() {
    // private var tracker: Tracker? = null  // Temporarily commented out
    private lateinit var stats1: TextView
    private lateinit var stats2: TextView
    private lateinit var stats3: TextView
    private lateinit var progress: ProgressBar
    
    /**
     * Make and show Toast message.
     * @param text resource id of string message
     */
    private fun showToastBox(text: Int) {
        runOnUiThread {
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showHelp() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }
    
    private fun showLicense() {
        val intent = Intent(this, LicenseActivity::class.java)
        startActivity(intent)
    }
    
    private fun showLeaveNoTrace() {
        val intent = Intent(this, LeaveNoTraceActivity::class.java)
        startActivity(intent)
    }
    
    private fun showSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    
    fun showRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    
    fun showBagNewPeak(view: View) {
        val intent = Intent(this, HikeActivity::class.java)
        startActivity(intent)
    }
    
    fun showAddOldPeak(view: View) {
        val intent = Intent(this, AddBagActivity::class.java)
        startActivity(intent)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as SummitRegister
        // tracker = application.getDefaultTracker()  // Temporarily commented out
        
        setContentView(R.layout.activity_home)
        
        stats1 = findViewById(R.id.peak_stats1)
        stats2 = findViewById(R.id.peak_stats2)
        stats3 = findViewById(R.id.peak_stats3)
        progress = findViewById(R.id.progressBar2)
        
        refreshStats()
    }
    
    private fun refreshStats() {
        val register = Register.getInstance(this)
        val totalUniqueSummits = register.getTotalUniqueSummits()
        val totalSummits = register.getTotalSummits()
        
        var lastSummit = "None"
        val lastEntry = register.getLastEntry()
        if (lastEntry != null) {
            lastSummit = lastEntry.getMountainName() ?: "Unknown"
        }
        
        stats1.text = "Bagged $totalUniqueSummits/53 14ers"
        stats2.text = "Total summits: $totalSummits"
        stats3.text = "Last summit: $lastSummit"
        
        progress.progress = totalUniqueSummits
    }
    
    override fun onResume() {
        super.onResume()
        refreshStats()
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.settings -> {
                showSettings()
                true
            }
            R.id.leave_no_trace -> {
                showLeaveNoTrace()
                true
            }
            R.id.license -> {
                showLicense()
                true
            }
            R.id.help -> {
                showHelp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.register, menu)
        return true
    }
}