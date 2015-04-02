package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.LoaderManager;
import android.widget.ListView;

import com.cpiekarski.fourteeners.R;
import com.cpiekarski.fourteeners.register.RegisterHelper;
import com.cpiekarski.fourteeners.utils.RegisterDate;
import com.cpiekarski.fourteeners.utils.SRLOG;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class RegisterActivity extends Activity /*implements
        LoaderManager.LoaderCallbacks<Cursor>*/ {
    private final String TAG = "RegisterHistoryActivity";
    
    private SimpleCursorAdapter mAdapter;
    
 
    private class MyAdapter extends SimpleCursorAdapter {
        public MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }
        
        private Date convertDate(String text) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
            Date formatedDate = sdf.parse(text);
            return formatedDate;
        }

        @Override
        public void setViewText(TextView v, String text) {
            if(v.getId() == R.id.peak_summit) {
                if("1".equals(text)) {
                    v.setText("Summited on:");
                } else {
                    v.setText("Attempted on:");
                }
            } else if (v.getId() == R.id.peak_elevation) {
                v.setText(text+" Feet");
            } else if (v.getId() == R.id.peak_summit_date) {
                SRLOG.v(TAG, text);
                RegisterDate dd = new RegisterDate(text);
                v.setText(""+(dd.getIntField(Calendar.MONTH)+1)+"/"+dd.getIntField(Calendar.DAY_OF_MONTH)+"/"+dd.getIntField(Calendar.YEAR));
            } else {
                v.setText(text);
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
//        String[] stringArray = new String[20];
//        for(int i = 0 ; i < 20; ++i) {
//            stringArray[i] = "Peak number " + i;
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.history_list_view, stringArray);
//        
        String[] fromCols = {RegisterHelper.MNT_NAME, RegisterHelper.PEEK_ELEVATION, RegisterHelper.SUMMIT, RegisterHelper.START_TIME};
        int[] toViews = {R.id.peak_name, R.id.peak_elevation, R.id.peak_summit, R.id.peak_summit_date};
        
        RegisterHelper r = new RegisterHelper(this);
        SQLiteDatabase d = r.getReadableDatabase();
        Cursor c = d.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, RegisterHelper.START_TIME+ " ASC");
        
        mAdapter = new MyAdapter(this, R.layout.history_list_view, c, fromCols, toViews, 0);
        ListView listView = (ListView) findViewById(R.id.history_listview);
        
        //android.R.drawable.divider_horizontal_dim_dark
        //android.R.drawable.divider_horizontal_dark
        listView.setAdapter(mAdapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

 /*   @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        RegisterHelper rh = new RegisterHelper(this);
        
        return new CursorLoader(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        mAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }*/
    
}