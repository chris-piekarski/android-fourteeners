package com.cpiekarski.fourteeners.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSummitRegisterTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllSummitRegisterTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(MountainsTest.class);
        suite.addTestSuite(RegisterEntryTest.class);
        suite.addTestSuite(RegisterTest.class);
        suite.addTestSuite(LocationTest.class);
        suite.addTestSuite(HikeActivityIsolationTest.class);
        suite.addTestSuite(HikeActivityFunctionalTest.class);
        //$JUnit-END$
        return suite;
    }
}