package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.os.Bundle;

import com.cpiekarski.fourteeners.R;


public class LicenseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}