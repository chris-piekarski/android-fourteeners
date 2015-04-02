package com.cpiekarski.fourteeners.test;

import android.content.Intent;

import android.test.ActivityUnitTestCase;

import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.activities.HikeActivity;

import junit.framework.Assert;

/** 
 * Isolation activity test. Doesn't interact with the rest of the system.
 */
public class HikeActivityIsolationTest extends ActivityUnitTestCase<HikeActivity> {

    // Activity of the Target application
    HikeActivity mainActivity;
    
    public HikeActivityIsolationTest() {
        super(HikeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the MainActivity of the target application
        startActivity(new Intent(getInstrumentation().getTargetContext(), HikeActivity.class), null, null);
        
        // Getting a reference to the MainActivity of the target application
        mainActivity = (HikeActivity)getActivity();
    }
    
    @SmallTest
    public void testTestIntent() {
        Intent intent = getStartedActivityIntent();
        Assert.assertFalse(mainActivity == null);
        Assert.assertFalse(intent != null);
    }
    
    public void testDeviceLocationObject() {
        Assert.assertFalse(false);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}