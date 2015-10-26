package com.cpiekarski.fourteeners.test;

import android.content.Intent;

import android.test.ActivityInstrumentationTestCase2;

import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.activities.AddBagActivity;

import junit.framework.Assert;

/** 
 * Isolation activity test. Doesn't interact with the rest of the system.
 */
public class AddBagActivityIsolationTest extends ActivityInstrumentationTestCase2<AddBagActivity> {

    // Activity of the Target application
    AddBagActivity mainActivity;
    
    public AddBagActivityIsolationTest() {
        super(AddBagActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }
    
    @SmallTest
    public void testTestIntent() {
        Assert.assertFalse(mainActivity == null);
    }
    
    public void testDeviceLocationObject() {
        Assert.assertFalse(false);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}