package com.cpiekarski.fourteeners.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.cpiekarski.fourteeners.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Maintains a TreeMap for known peaks.
 * 
 * 
 */
public class Mountains {
    
    private final String TAG = "Mountains";
    private TreeMap<String, Mountain> mMnts;
    private Context mCtx;
    // Analytics removed


    private TreeMap<String, ArrayList<String>> mRanges;
    
    public static Mountains instance = null;
    //private TreeMap<Location, String> mLocs;
    
    /**
     * Use Mountains class as a singleton
     * @param context
     * @return
     */
    public static Mountains getInstance(Context context) {
        if(instance == null) {
            instance = new Mountains(context);
            instance.parseFourteeners();
        }
        return instance;
    }
    
    public Mountains(Context context) {
        mMnts = new TreeMap<String, Mountain>();
        mCtx = context;
        mRanges = new TreeMap<String, ArrayList<String>>();

        // no-op: Application subclass removed
    }
    
    public String[] getRanges() {
        return (String[]) mRanges.keySet().toArray(new String[7]);
    }
    
    public String[] getNamesInRange(String range) {
        if ( mRanges.containsKey(range) ) {
            ArrayList<String> al = mRanges.get(range);
            return al.toArray(new String[0]);
        }
        return null;
    }
    
    public String[] getAllPeakNames() {
        return mMnts.keySet().toArray(new String[0]);
    }
    
    /**
     * O(C)
     * @return the number of peaks in the list
     */
    public int getSize() {
        return mMnts.size();
    }
    
    /**
     * Get a known mountain by name only.
     * @param name
     * @return
     */
    public Mountain getMountain(String name) {
        Mountain mnt = mMnts.get(name);
        return mnt;
    }
    
    /**
     * Get a known mountain by name and range.
     * O(log n)
     * 
     * @param name of the Mountain
     * @param range the Mountain is in
     * @return The correct mountain object
     */
    public Mountain getMountain(String name, String range) {
        //TODO: add range check
        Mountain mnt = mMnts.get(name);
        return mnt;
    }
    
    /**
     * O(n log n) add and sort by name
     */
    public void parseFourteeners() {
        SRLOG.v(TAG, "Parsing fourteener data...");
        Resources res = mCtx.getResources();
        XmlResourceParser xrp = res.getXml(R.xml.mountain_data);
        try{
            xrp.next(); // skip first 'mountains' element
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                xrp.next(); // get first 'mountain' element
                if(xrp.getEventType() == XmlResourceParser.START_TAG) {
                    // double check its the right element
                    if(xrp.getName().equals("mountain")) {
                        // extract the data you want
                        int count = xrp.getAttributeCount();
                        String name = xrp.getAttributeValue(null, "name");
                        String rank = xrp.getAttributeValue(null, "rank");
                        String elev = xrp.getAttributeValue(null, "elevation");
                        String range = xrp.getAttributeValue(null, "range");
                        String longitude = xrp.getAttributeValue(null, "long");
                        String latitude = xrp.getAttributeValue(null, "lat");
                        String county = xrp.getAttributeValue(null, "county");

                        Mountain m = new Mountain(name, range , county, Double.parseDouble(longitude), 
                                Double.parseDouble(latitude), Integer.parseInt(rank), Integer.parseInt(elev));
                        mMnts.put(name, m);
                        
                        if(mRanges.containsKey(range)) {
                            ArrayList<String> al = mRanges.get(range);
                            al.add(name);
                        } else {
                            ArrayList<String> nal = new ArrayList<String>();
                            nal.add(name);
                            mRanges.put(range, nal);
                        }
                        
                        SRLOG.v(TAG, "Mountain Attribute Count " + count);
                        SRLOG.v(TAG, "Peak Name " + name);
                        SRLOG.v(TAG, "Peak Elevation " + elev);
                    }
                }
            }
        } catch (Exception e) {
            SRLOG.e(TAG, e.toString());
            // no-op
        } finally {
            xrp.close();
            SRLOG.i(TAG, "Fourteener data parsed");
        }
    }

}
