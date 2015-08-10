package com.cpiekarski.fourteeners.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.utils.Mountain;
import com.cpiekarski.fourteeners.utils.Mountains;

import junit.framework.Assert;
import android.util.Log;

public class MountainsTest extends AndroidTestCase {

    private Mountains mMountains;
    private final String TAG = "SummitMountainsTest";
    
    public MountainsTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        mMountains = new Mountains(getContext());
        mMountains.parseFourteeners();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        mMountains = null;
    }
    
    public void testSize() {
        Assert.assertTrue("size not 58", mMountains.getSize() == 58);
    }
    
    @SmallTest
    public void testLongs() {
        Mountain m = mMountains.getMountain("Longs Peak");
        Assert.assertTrue("Elevation", 14255 == m.getElevation());
        Assert.assertTrue("Range", "Front".equals(m.getRange()));
        Assert.assertTrue("County", "Boulder".equals(m.getCounty()));
    }
    
    @SmallTest
    public void testBlanca() {
        Mountain m = mMountains.getMountain("Blanca Peak");
        Assert.assertTrue("Elevation", 14345 == m.getElevation());
        Assert.assertTrue("Range", "Sangre de Cristo".equals(m.getRange()));
        Assert.assertTrue("County", "Alamosa, Huerfano, Costilla".equals(m.getCounty()));
    }
    
    @SmallTest
    public void testSingleton() {
        Mountain m = Mountains.getInstance(getContext()).getMountain("Longs Peak");
        Assert.assertTrue("Elevation", 14255 == m.getElevation());
        Assert.assertTrue("Range", "Front".equals(m.getRange()));
        Assert.assertTrue("County", "Boulder".equals(m.getCounty()));
    }
    
    @SmallTest
    public void testRanges() {
        String[] ranges = mMountains.getRanges();
        Assert.assertTrue("Ranges not 7", 7 == ranges.length);
    }
    
    @SmallTest
    public void testFrontRange() {
        String[] front = mMountains.getNamesInRange("Front");
        Assert.assertTrue("Front range not 6", 6 == front.length);
    }
    
    @SmallTest
    public void testElkRange() {
        String[] front = mMountains.getNamesInRange("Elk");
        Assert.assertTrue("Elk range not 7", 7 == front.length);
    }
    
    @SmallTest
    public void testSawatchRange() {
        String[] front = mMountains.getNamesInRange("Sawatch");
        Assert.assertTrue("Sawatch range not 15", 15 == front.length);
    }
}