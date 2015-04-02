package com.cpiekarski.fourteeners.register;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cpiekarski.fourteeners.utils.Mountain;
import com.cpiekarski.fourteeners.utils.Mountains;
import com.cpiekarski.fourteeners.utils.SRLOG;

/**
 * Utils class for Register information.
 * 
 * O(1)
 */
public class Register {
    public final int NO_VALUE = -1;
    private final String TAG = "Register";
    private Context mCtx;
    
    public static Register instance = null;
    
    private RegisterHelper r;
    private SQLiteDatabase d;

    
    /**
     * Use Register class as a singleton
     * @param context
     * @return
     */
    public static Register getInstance(Context context) {
        if(instance == null) {
            instance = new Register(context);
        }
        return instance;
    }
    
    public Register(Context context) {
        mCtx = context;
        r = new RegisterHelper(mCtx);
        d = r.getReadableDatabase();
    }
    
    public int getTotalEntries() {

        String[] cols = new String[1];
        cols[0] = RegisterHelper.MNT_NAME;
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, cols, null, null, null, null, null);
        int total = c.getCount();
        SRLOG.v(TAG, "Total entries is: "+total);
        c.close();
        return total;
    }
    
    public int getTotalSummits() {
        
        String[] cols = new String[1];
        cols[0] = RegisterHelper.MNT_NAME;
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, cols, RegisterHelper.SUMMIT+" IS 1", null, null, null, null);
        int total = c.getCount();
        SRLOG.v(TAG, "Total summits is: "+total);

        c.close();
        return total;
    }
    
    public int getTotalSummits(String mountain) {

        
        String[] cols = new String[1];
        cols[0] = RegisterHelper.MNT_NAME;
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, cols, 
                RegisterHelper.SUMMIT+" IS 1 AND "+RegisterHelper.MNT_NAME+" IS "+mountain, null, null, null, null);
        int total = c.getCount();
        SRLOG.v(TAG, "Total summits for "+mountain+" is: "+total);

        c.close();
        return total;
    }
    
    public int getTotalUniqueSummits() {
        String[] cols = new String[1];
        cols[0] = RegisterHelper.MNT_NAME;
        
        //distinct query
        Cursor c = d.query(true, RegisterHelper.TABLE_NAME, cols, RegisterHelper.SUMMIT+" IS 1", null, null, null, null, null);
        int total = c.getCount();
        SRLOG.v(TAG, "Total unique summits is: "+total);

        c.close();
        return total;
    }
    
    /**
     * Gets the last entry in the register with a successful summit.
     * 
     * @return Entry or null;
     */
    public RegisterEntry getLastEntry() {
        String[] cols = new String[1];
        cols[0] = "_id";
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, cols, 
                RegisterHelper.SUMMIT+" IS 1", null, null, null, "_id DESC", "1");
        
        if(c.getCount() == 0) {
            c.close();
            return null;
        }
        
        c.moveToNext();
        
        RegisterEntry re = new RegisterEntry(mCtx, c.getInt(c.getColumnIndex("_id")));
        c.close();
        return re;
    }
    
}