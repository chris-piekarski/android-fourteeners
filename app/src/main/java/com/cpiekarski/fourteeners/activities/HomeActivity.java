package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpiekarski.fourteeners.R;
import com.cpiekarski.fourteeners.SummitRegister;
import com.cpiekarski.fourteeners.register.Register;
import com.cpiekarski.fourteeners.register.RegisterEntry;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class HomeActivity extends Activity {
    private Tracker mTracker;

    private TextView mStats1;
    private TextView mStats2;
    private TextView mStats3;
    
    private ProgressBar mProgress;
    
    /**
     * Make and show Toast message.
     * @param text resource id of string message
     * {@code showToastBox(R.string.sdcard_not_readable);}
     */
    private void showToastBox(final int text) {
        runOnUiThread(new Runnable() {
             public void run() {
                 Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);    
    }
    
    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    public void showRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    
    public void showBagNewPeak(View view) {
        Intent intent = new Intent(this, HikeActivity.class);
        startActivity(intent);
    }
    
    public void showAddOldPeak(View view) {
        Intent intent = new Intent(this, AddBagActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SummitRegister application = (SummitRegister) getApplication();
        mTracker = application.getDefaultTracker();

        setContentView(R.layout.activity_home);
        
        mStats1 = (TextView) findViewById(R.id.peak_stats1);
        mStats2 = (TextView) findViewById(R.id.peak_stats2);
        mStats3 = (TextView) findViewById(R.id.peak_stats3);
        mProgress = (ProgressBar) findViewById(R.id.progressBar2);
        
        refreshStats();
    }
    
    private void refreshStats() {
        int tus = Register.getInstance(this).getTotalUniqueSummits();
        int ts = Register.getInstance(this).getTotalSummits();
        
        String lastSummit = "None";
        RegisterEntry re = Register.getInstance(this).getLastEntry();
        if(re != null) {
            lastSummit = re.getMountainName();
        }
        
        mStats1.setText("Bagged "+tus+"/53 14ers");
        mStats2.setText("Total summits: "+ts);
        mStats3.setText("Last summit: "+lastSummit);
        
        mProgress.setProgress(tus);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshStats();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                showSettings();
                return true;
            case R.id.help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }
}