package com.cpiekarski.fourteeners.test;

import android.content.Intent;

import android.test.ActivityUnitTestCase;

import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.activities.AddBagActivity;

import junit.framework.Assert;

/** 
 * Isolation activity test. Doesn't interact with the rest of the system.
 */
public class AddBagActivityIsolationTest extends ActivityUnitTestCase<AddBagActivity> {

    // Activity of the Target application
    AddBagActivity mainActivity;
    
    public AddBagActivityIsolationTest() {
        super(AddBagActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the MainActivity of the target application
        startActivity(new Intent(getInstrumentation().getTargetContext(), AddBagActivity.class), null, null);
        
        // Getting a reference to the MainActivity of the target application
        mainActivity = (AddBagActivity)getActivity();
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