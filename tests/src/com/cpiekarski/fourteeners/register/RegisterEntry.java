package com.cpiekarski.fourteeners.register;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cpiekarski.fourteeners.utils.Mountain;
import com.cpiekarski.fourteeners.utils.Mountains;
import com.cpiekarski.fourteeners.utils.SRLOG;

/**
 * CRUD model for a register entry
 */
public class RegisterEntry {
    public final int NO_VALUE = -1;
    private final String TAG = "RegisterEntry";
    private Context mCtx;
    
    private Mountain mMountain;
    private String mStartTime;
    private String mEndTime;
    private String mDistance;
    
    private int mStartElevation = NO_VALUE;
    private int mEndElevation = NO_VALUE;
    private int mPeekElevation = NO_VALUE;
    private int mSummit = NO_VALUE;
    
    private double mStartLatLoc = NO_VALUE;
    private double mStartLongLoc = NO_VALUE;
    private double mEndLatLoc = NO_VALUE;
    private double mEndLongLoc = NO_VALUE;
    
    private int mEndLocAccuracy = NO_VALUE;
    private int mStartLocAccuracy = NO_VALUE;
    
    private String mNotes;
    private long mRowId;
    private String mProof;
    
    public RegisterEntry(Context context) {
        mCtx = context;
    }
    
    public RegisterEntry(Context context, int entryId) {
        mCtx = context;
        readEntry(entryId);
    }
    
    private String getStartLocationTuple() {
        return String.valueOf(mStartLatLoc) + ";" + String.valueOf(mStartLongLoc) + ";" +
                String.valueOf(mStartLocAccuracy);
    }
    
    private String getEndLocationTuple() {
        return String.valueOf(mEndLatLoc) + ";" + String.valueOf(mEndLongLoc) + ";" +
                String.valueOf(mEndLocAccuracy);
    }
    
    private void parseStartLocationTuple(String tuple) {
        String[] x = tuple.split(";");
        mStartLatLoc = Double.parseDouble(x[0]);
        mStartLongLoc = Double.parseDouble(x[1]);
        mStartLocAccuracy = Integer.parseInt(x[2]);
    }
    
    private void parseEndLocationTuple(String tuple) {
        String[] x = tuple.split(";");
        mEndLatLoc = Double.parseDouble(x[0]);
        mEndLongLoc = Double.parseDouble(x[1]);
        mEndLocAccuracy = Integer.parseInt(x[2]);
    }
    
    /**
     * 
     * @return false if failure, true otherwise
     */
    public boolean createEntry() {
        if(mMountain == null) {
            SRLOG.w(TAG, "No mountain set for entry; entry not created.");
            return false;
        }
//        } else if(mStartLatLoc == null) {
//            SRLOG.w(TAG, "No start location set for entry; entry not created.");
//            return false;
//        }
        
        
        RegisterHelper r = new RegisterHelper(mCtx);
        SQLiteDatabase d = r.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(RegisterHelper.MNT_NAME, getMountainName());
        values.put(RegisterHelper.MNT_RANGE, getMountainRange());
        values.put(RegisterHelper.START_ELEVATION, mStartElevation);
        values.put(RegisterHelper.START_TIME, mStartTime);
        values.put(RegisterHelper.START_LOC, getStartLocationTuple());
        values.put(RegisterHelper.END_ELEVATION, mEndElevation);
        values.put(RegisterHelper.END_TIME, mEndTime);
        values.put(RegisterHelper.END_LOC, getEndLocationTuple());
        values.put(RegisterHelper.PEEK_ELEVATION, mPeekElevation);
        values.put(RegisterHelper.DISTANCE, mDistance);
        values.put(RegisterHelper.NOTES, mNotes);
        values.put(RegisterHelper.PROOF, mProof);
        values.put(RegisterHelper.SUMMIT, mSummit);
        
        mRowId = d.insert(RegisterHelper.TABLE_NAME, null, values);
        
        if(mRowId == -1) {
            SRLOG.e(TAG, "Failed to insert new entry; rowId = -1.");
        } else {
            SRLOG.d(TAG, "Register entry created; rowId = " + mRowId);
        }
        
        return mRowId == -1 ? false : true;
    }
    
    public boolean readEntry(int e) {
        boolean result = true;
        RegisterHelper r = new RegisterHelper(mCtx);
        SQLiteDatabase d = r.getReadableDatabase();
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, null,"_id = " + e, 
                null, null, null, null);
        
        if(c.getCount() > 0) {
            if(c.moveToNext()) {
                mRowId = c.getLong(c.getColumnIndex("_id"));
                
                int i = c.getColumnIndex(RegisterHelper.NOTES);
                mNotes = c.getString(i);
                
                i = c.getColumnIndex(RegisterHelper.DISTANCE);
                mDistance = c.getString(i);
                
                i = c.getColumnIndex(RegisterHelper.START_LOC);
                parseStartLocationTuple(c.getString(i));
                
                i = c.getColumnIndex(RegisterHelper.END_LOC);
                parseEndLocationTuple(c.getString(i));
        
                i = c.getColumnIndex(RegisterHelper.MNT_NAME);
                String name = c.getString(i);
                
                i = c.getColumnIndex(RegisterHelper.MNT_RANGE);
                String range = c.getString(i);
                
                mMountain = Mountains.getInstance(mCtx).getMountain(name, range);
            }
        } else {
            SRLOG.w(TAG, "No row found for entry number "+e);
            result =  false;
        }
        
        c.close();
        
        return result;
    }
    
    public boolean updateEntry() {
        RegisterHelper r = new RegisterHelper(mCtx);
        SQLiteDatabase d = r.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(RegisterHelper.MNT_NAME, getMountainName());
        values.put(RegisterHelper.MNT_RANGE, getMountainRange());
        values.put(RegisterHelper.START_ELEVATION, mStartElevation);
        values.put(RegisterHelper.START_TIME, mStartTime);
        values.put(RegisterHelper.START_LOC, getStartLocationTuple());
        values.put(RegisterHelper.END_ELEVATION, mEndElevation);
        values.put(RegisterHelper.END_TIME, mEndTime);
        values.put(RegisterHelper.END_LOC, getEndLocationTuple());
        values.put(RegisterHelper.PEEK_ELEVATION, mPeekElevation);
        values.put(RegisterHelper.DISTANCE, mDistance);
        values.put(RegisterHelper.NOTES, mNotes);
        values.put(RegisterHelper.PROOF, mProof);
        values.put(RegisterHelper.SUMMIT, mSummit);
        
        int x = d.update(RegisterHelper.TABLE_NAME, values, "_id = " + mRowId, null);
        
        if(x == 0) {
            SRLOG.e(TAG, "Failed to update entry; updated 0 rows.");
        } else {
            SRLOG.d(TAG, "Register entry updated; rowId = " + mRowId);
        }
        
        return mRowId == -1 ? false : true;
    }
    
    public boolean deleteEntry() {
        RegisterHelper r = new RegisterHelper(mCtx);
        SQLiteDatabase d = r.getWritableDatabase();
        
        int x = d.delete(RegisterHelper.TABLE_NAME, "_id = " + mRowId, null);
        
        return x == 0 ? false : true;
    }
    
    public String getMountainName() {
        return mMountain.getName();
    }
    
    public String getMountainRange() {
        return mMountain.getRange();
    }
    
    /**
     * Entries can be added for failed summit attempts. If the summit was reached
     * pass a true to this method.
     * @param summit
     */
    public void setReachedSummit(boolean summit) {
        mSummit = summit ? 1 : 0;
    }
    
    public void setProof(String proof) {
        mProof = proof;
    }
    
    public void setNotes(String notes) {
        mNotes = notes;
    }
    
    public void setMountain(Mountain mnt) {
        mMountain = mnt;
    }
    
    public void setStartLoc(double lat, double lon, int accuracy) {
        mStartLatLoc = lat;
        mStartLongLoc = lon;
        mStartLocAccuracy = accuracy;
    }
    
    public void setEndLoc(double lat, double lon, int accuracy) {
        mEndLatLoc = lat;
        mEndLongLoc = lon;
        mEndLocAccuracy = accuracy;
    }
    
    public void setStartElevation(int elev) {
        mStartElevation = elev;
    }
    
    public void setEndElevation(int elev) {
        mEndElevation = elev;
    }
    
    public void setPeekElevation(int elev) {
        mPeekElevation = elev;
    }
    
    public void setStartTime(String time) {
        mStartTime = time;
    }
    
    public void setEndTime(String time) {
        mEndTime = time;
    }
    
    public void setDistance(String distance) {
        mDistance = distance;
    }
    
    @Override
    public String toString() {
        return "";
    }
}
