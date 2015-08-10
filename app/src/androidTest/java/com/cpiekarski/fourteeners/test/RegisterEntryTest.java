package com.cpiekarski.fourteeners.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.register.RegisterEntry;
import com.cpiekarski.fourteeners.register.RegisterHelper;
import com.cpiekarski.fourteeners.utils.Mountains;
import com.cpiekarski.fourteeners.utils.SRLOG;

import junit.framework.Assert;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

public class RegisterEntryTest extends AndroidTestCase {

    private RegisterEntry mEntry;
    private final String TAG = "RegisterEntryTest";
    
    public RegisterEntryTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        //clearAllRows();
        deleteDatabase();
        mEntry = new RegisterEntry(getContext());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        mEntry = null;
        clearAllRows();
    }
    
    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        String formatedDate = sdf.format(new Date(System.currentTimeMillis()));
        return formatedDate;
    }
    
    private String setFalseEntryData(String mtn, int i) {
        String ts = getTimeStamp();
        mEntry.setMountain(Mountains.getInstance(getContext()).getMountain(mtn));
        mEntry.setNotes("This is a JUnit test insert");
        mEntry.setStartTime(ts);
        mEntry.setDistance(String.valueOf(12345+i));
        mEntry.setStartElevation(10000+i);
        mEntry.setStartLoc(1.0+i, 1.0+i, 100+i);
        mEntry.setReachedSummit(true);
        return ts;
    }
    
    private void deleteDatabase() {
        RegisterHelper r = new RegisterHelper(getContext());
        SQLiteDatabase d = r.getWritableDatabase();
                
        d.close();
        r.close();
        
        SRLOG.d(TAG, d.getPath());
        Assert.assertTrue("delete not true", true == SQLiteDatabase.deleteDatabase(new File(d.getPath())));
    }
    
    private void clearAllRows() {
        RegisterHelper r = new RegisterHelper(getContext());
        SQLiteDatabase d = r.getWritableDatabase();
        
        d.delete(RegisterHelper.TABLE_NAME, null, null);
        
        d.close();
        r.close();
        
    }
    
    @SmallTest
    public void testSingleInsert() {

        String ts = setFalseEntryData("Longs Peak", 1);
        Assert.assertTrue(mEntry.createEntry());
        
        RegisterHelper r = new RegisterHelper(getContext());
        SQLiteDatabase d = r.getReadableDatabase();
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, null);
        
        Assert.assertTrue("Count is not 1", 1 == c.getCount());

        c.moveToNext();
        
        Assert.assertEquals("Front", c.getString(c.getColumnIndex(RegisterHelper.MNT_RANGE)));
        Assert.assertEquals("Longs Peak", c.getString(c.getColumnIndex(RegisterHelper.MNT_NAME)));
        Assert.assertEquals(ts, c.getString(c.getColumnIndex(RegisterHelper.START_TIME)));
        Assert.assertEquals(Integer.parseInt("12345")+1, Integer.parseInt(c.getString(c.getColumnIndex(RegisterHelper.DISTANCE))));
        Assert.assertEquals(10001, c.getInt(c.getColumnIndex(RegisterHelper.START_ELEVATION)));
        c.close();
        
        mEntry.deleteEntry();
        
        c = d.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, null);
        
        Assert.assertTrue("Count is not 0", 0 == c.getCount());
        
        c.close();
        d.close();
        r.close();
    }
    
    @MediumTest
    public void testFiftyInsert() {
        String[] tss = new String[50];
        for(int i = 0; i < 50; ++i) {
            String ts = setFalseEntryData("Longs Peak", i);
            tss[i] = ts;
            Assert.assertTrue(mEntry.createEntry());
        }
        
        RegisterHelper r = new RegisterHelper(getContext());
        SQLiteDatabase d = r.getReadableDatabase();
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, null);
        
        Assert.assertTrue("Count is not 50", 50 == c.getCount());
        
        int i = 0;
        while(!c.moveToNext()) {
            Assert.assertEquals("Front", c.getString(c.getColumnIndex(RegisterHelper.MNT_RANGE)));
            Assert.assertEquals("Longs Peak", c.getString(c.getColumnIndex(RegisterHelper.MNT_NAME)));
            Assert.assertEquals(tss[i], c.getString(c.getColumnIndex(RegisterHelper.START_TIME)));
            Assert.assertEquals(Integer.parseInt("12345")+i, Integer.parseInt(c.getString(c.getColumnIndex(RegisterHelper.DISTANCE))));
            Assert.assertEquals(10000+i, c.getInt(c.getColumnIndex(RegisterHelper.START_ELEVATION)));
            ++i;
        }
        
        c.close();
        r.close();
        d.close();
    }
    
    @LargeTest
    public void testHundredInsert() {
        String[] tss = new String[100];

        for(int i = 0; i < 100; ++i) {
            String ts = setFalseEntryData("Longs Peak", i);
            tss[i] = ts;
            Assert.assertTrue(mEntry.createEntry());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.v(TAG, "Error "+e.toString());
            }
        }
        
        RegisterHelper r = new RegisterHelper(getContext());
        SQLiteDatabase d = r.getReadableDatabase();
        
        Cursor c = d.query(RegisterHelper.TABLE_NAME, null, null, null, null, null, null);
        
        Assert.assertTrue("Count is not 100", 100 == c.getCount());
        
        int i = 0;
        while(!c.moveToNext()) {
            Assert.assertEquals("Front", c.getString(c.getColumnIndex(RegisterHelper.MNT_RANGE)));
            Assert.assertEquals("Longs Peak", c.getString(c.getColumnIndex(RegisterHelper.MNT_NAME)));
            Assert.assertEquals(tss[i], c.getString(c.getColumnIndex(RegisterHelper.START_TIME)));
            Assert.assertEquals(Integer.parseInt("12345")+i, Integer.parseInt(c.getString(c.getColumnIndex(RegisterHelper.DISTANCE))));
            Assert.assertEquals(10000+i, c.getInt(c.getColumnIndex(RegisterHelper.START_ELEVATION)));
            ++i;
        }

    
        c.close();
        r.close();
        d.close();
    }
}