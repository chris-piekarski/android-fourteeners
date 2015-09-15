package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.os.Bundle;

import com.cpiekarski.fourteeners.R;


public class LeaveNoTraceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_no_trace);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}