package com.cpiekarski.fourteeners.test;

import android.test.AndroidTestCase;

import com.cpiekarski.fourteeners.utils.RegisterDate;
import com.cpiekarski.fourteeners.utils.SRLOG;

import junit.framework.Assert;

import java.util.Calendar;

public class RegisterDateTest extends AndroidTestCase {

    private RegisterDate mDate;
    private final String TAG = "RegisterDateTest";
    
    public RegisterDateTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        mDate = new RegisterDate();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        mDate = null;
    }
    
    public void testSimple() {
        
    }
    
    public void testFormatMom() {
        mDate.setDate(Calendar.FEBRUARY, 11, 1953);
        mDate.setTime(0, 0, 0);
        String mom = mDate.getStrDate();
        SRLOG.v(TAG, mom);
        Assert.assertTrue("format date not right", "1953-02-11T00:00:00-0700".equals(mom));
    }
    
    public void testParseMom() {
        Assert.assertTrue(mDate.parseStrDate("1953-02-11T00:11:12-0700"));
        SRLOG.v(TAG, "month " + mDate.getIntField(Calendar.MONTH));
        Assert.assertTrue("month not right", mDate.getIntField(Calendar.MONTH) == Calendar.FEBRUARY);
        Assert.assertTrue("year not right", mDate.getIntField(Calendar.YEAR)==1953);
        Assert.assertTrue("day not right", mDate.getIntField(Calendar.DAY_OF_MONTH) == 11);
        Assert.assertTrue("hour not right", mDate.getIntField(Calendar.HOUR) == 0);
        Assert.assertTrue("minute not right", mDate.getIntField(Calendar.MINUTE)==11);
        Assert.assertTrue("second not right", mDate.getIntField(Calendar.SECOND)==12);
    }
    
    public void testFormatter() {
        Assert.assertTrue("unexpected formatter", mDate.getFormatter().equals("yyyy-MM-dd'T'HH:mm:ssZ"));
    }
}