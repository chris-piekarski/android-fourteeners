package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.os.Bundle;

import com.cpiekarski.fourteeners.utils.DeviceLocation;


public class HikeActivity extends Activity {
    DeviceLocation dl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dl = new DeviceLocation(this);
        dl.getPassiveUpdates();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}