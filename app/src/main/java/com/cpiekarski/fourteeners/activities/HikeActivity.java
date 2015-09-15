package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.os.Bundle;

import com.cpiekarski.fourteeners.SummitRegister;
import com.cpiekarski.fourteeners.utils.DeviceLocation;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class HikeActivity extends Activity {
    DeviceLocation dl;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SummitRegister application = (SummitRegister) getApplication();
        mTracker = application.getDefaultTracker();

        dl = new DeviceLocation(this);
        dl.getPassiveUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}