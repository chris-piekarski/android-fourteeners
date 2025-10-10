package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cpiekarski.fourteeners.R;
import com.cpiekarski.fourteeners.SummitRegister;
import com.cpiekarski.fourteeners.utils.DeviceLocation;
import com.cpiekarski.fourteeners.utils.Mountain;
import com.cpiekarski.fourteeners.utils.SRLOG;
// Analytics removed

import java.util.ArrayList;

public class HikeActivity extends Activity {
    DeviceLocation dl;
    
    private MyAdapter mAdapter;
    private final String TAG = "HikeActivity";

    private class MyAdapter extends ArrayAdapter<Mountain> {
        public MyAdapter(Context context, int resource, ArrayList<Mountain> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Mountain m = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.hike_list_view, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.peak_name);
            TextView elevation = (TextView) convertView.findViewById(R.id.peak_elevation_pick);

            name.setText(m.getName());
            elevation.setText(String.valueOf(m.getElevation()));
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike);

        SummitRegister application = (SummitRegister) getApplication();

        dl = new DeviceLocation(this);
        dl.getPassiveUpdates();


    }

    public void manualPick(View view) {
        SRLOG.v(TAG, "Manual pick clicked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Mountain> m = dl.getNearestMountains(10);

        mAdapter = new MyAdapter(this, R.layout.hike_list_view,m);
        ListView listView = (ListView) findViewById(R.id.nearest_listview);
        listView.setAdapter(mAdapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}