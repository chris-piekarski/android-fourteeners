package com.cpiekarski.fourteeners.test;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.activities.HikeActivity;

import junit.framework.Assert;

/** 
 * Functional activity test. Interact with the rest of the system.
 */
public class HikeActivityFunctionalTest extends ActivityInstrumentationTestCase2<HikeActivity> {

    // Activity of the Target application
    HikeActivity mainActivity;
    
    public HikeActivityFunctionalTest() {
        super(HikeActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testActivityStarted() {
        Assert.assertTrue(mainActivity != null);
    }
    
    public void testDelay() throws InterruptedException {
        Thread.sleep(60000);
        Assert.assertTrue(true);
    }
}