package com.cpiekarski.fourteeners.activities

import android.app.Activity
import android.os.Bundle
import com.cpiekarski.fourteeners.R

class LicenseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}