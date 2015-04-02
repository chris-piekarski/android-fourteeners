package com.cpiekarski.fourteeners.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.register.Register;
import com.cpiekarski.fourteeners.register.RegisterEntry;
import com.cpiekarski.fourteeners.register.RegisterHelper;
import com.cpiekarski.fourteeners.utils.Mountains;

import junit.framework.Assert;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

public class RegisterTest extends AndroidTestCase {

    private RegisterEntry mEntry;
    private final String TAG = "RegisterTest";
    
    public RegisterTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        clearAllRows();
        mEntry = new RegisterEntry(getContext());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        clearAllRows();
        mEntry = null;
    }
    
    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        String formatedDate = sdf.format(new Date(System.currentTimeMillis()));
        return formatedDate;
    }
    
    private String setFalseEntryData(String mtn, int i, boolean summit) {
        String ts = getTimeStamp();
        mEntry.setMountain(Mountains.getInstance(getContext()).getMountain(mtn));
        mEntry.setNotes("This is a JUnit test insert");
        mEntry.setStartTime(ts);
        mEntry.setDistance(String.valueOf(12345+i));
        mEntry.setStartElevation(10000+i);
        mEntry.setStartLoc(1.0+i, 1.0+i, 100+i);
        mEntry.setReachedSummit(summit);
        return ts;
    }
    
    private void clearAllRows() {
        RegisterHelper r = new RegisterHelper(getContext());
        SQLiteDatabase d = r.getWritableDatabase();
        
        d.delete(RegisterHelper.TABLE_NAME, null, null);
        
        d.close();
        r.close();
    }
    
    @SmallTest
    public void testLastEntry() {
        String[] tss = new String[50];
        for(int i = 0; i < 50; ++i) {
            String ts = setFalseEntryData("Longs Peak", i, true);
            tss[i] = ts;
            Assert.assertTrue(mEntry.createEntry());
        }
        
        RegisterEntry lastEntry = Register.getInstance(getContext()).getLastEntry();
                
        Assert.assertEquals("Front", lastEntry.getMountainRange());
        Assert.assertEquals("Longs Peak", lastEntry.getMountainName());
    }
    
    @MediumTest
    public void testTotalEntries() {
        for(int i = 0; i < 100; ++i) {
            setFalseEntryData("Longs Peak", i, false);
            Assert.assertTrue(mEntry.createEntry());
        }
        
                
        Assert.assertTrue("Total entries not 100", 100 == Register.getInstance(getContext()).getTotalEntries());
    }
    
    @MediumTest
    public void testTotal100Summits() {
        for(int i = 0; i < 100; ++i) {
            setFalseEntryData("Longs Peak", i, true);
            Assert.assertTrue(mEntry.createEntry());
        }
        
                
        Assert.assertTrue("Total summits not 100", 100 == Register.getInstance(getContext()).getTotalSummits());
    }
    
    @LargeTest
    public void testTotal200SummitMix() {
        for(int i = 0; i < 200; ++i) {
            setFalseEntryData("Longs Peak", i, i % 2 == 0 ? true : false);
            Assert.assertTrue(mEntry.createEntry());
        }
        
        Assert.assertTrue("Total summits not 200", 200 == Register.getInstance(getContext()).getTotalEntries());
        Assert.assertTrue("Total summits not 100", 100 == Register.getInstance(getContext()).getTotalSummits());
        Assert.assertTrue("Total unique summits not 1", 1 == Register.getInstance(getContext()).getTotalUniqueSummits());
    }
}