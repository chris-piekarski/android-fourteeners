package com.cpiekarski.fourteeners.utils;

public class Mountain {
    private String mName;
    private String mRange;
    private String mCounty;
    private int mRank;
    private int mElevation;
    private double mLongitude;
    private double mLatitude;
    
    private final String TAG = "Mountain";
    public static int NONE_OFFICIAL_RANK = -1;
    
    /**
     * 
     * @param name Name of the peak
     * @param range What range the peak is in
     * @param longitude Geo location
     * @param latitude Geo location
     * @param rank Arbitrary rank
     * @param elevation The peak elevation
     */
    Mountain(String name, String range, String county, double longitude, double latitude
            ,int rank
            ,int elevation ) {
        mName = name;
        mRange = range;
        mElevation = elevation;
        mRank = rank;
        mLongitude = longitude;
        mLatitude = latitude;
        mCounty = county;
    }
    
    public double getLongitude() {
        return mLongitude;
    }
    
    public double getLatitude() {
        return mLatitude;
    }
    
    public String getName() {
        return mName;
    }
    
    public String getRange() {
        return mRange;
    }
    
    public String getCounty() {
        return mCounty;
    }
    
    public int getElevation() {
        return mElevation;
    }
    
    @Override
    public String toString() {
        return "Name: " + mName + " Range: " + mRange + " Elevation: " + mElevation;
    }
}
