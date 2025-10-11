package com.cpiekarski.fourteeners.activities

import android.app.Activity
import android.os.Bundle
import com.cpiekarski.fourteeners.R

class LeaveNoTraceActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_no_trace)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}