package com.cpiekarski.fourteeners.test;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.cpiekarski.fourteeners.utils.DeviceLocation;
import com.cpiekarski.fourteeners.utils.Mountain;
import com.cpiekarski.fourteeners.utils.Mountains;
import com.cpiekarski.fourteeners.utils.SRLOG;
import com.cpiekarski.fourteeners.utils.DeviceLocation.LocationType;

import junit.framework.Assert;

import java.util.LinkedList;

import android.util.Log;

public class LocationTest extends AndroidTestCase {

    private DeviceLocation mLocation;
    private final String TAG = "LocationTest";
    private String mockProvider = DeviceLocation.MOCK_PROVIDER;
    private Location mNewTestLocation;
    
    private void addMockProvider() {
        LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        
        if(null == locationManager.getProvider(mockProvider)) {
            locationManager.addTestProvider(mockProvider, false, false, true, false, true, true, true, Criteria.POWER_MEDIUM, Criteria.ACCURACY_FINE);
        }
        locationManager.setTestProviderEnabled(mockProvider, true);
        mLocation.getMockUpdates(); 
    }
    
    private void removeMockProvider() {
        LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        mLocation.stopUpdates();
        locationManager.removeTestProvider(mockProvider);
    }
    
    private void setMockLocation(Location loc) {
        LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.setTestProviderLocation(mockProvider, loc);
    }
    
    public LocationTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        mLocation = new DeviceLocation(getContext());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        //mLocation.stopUpdates();
        mLocation = null;
    }
    
    public Location createLocation(double lat, double lng, float accuracy) {
        // Create a new Location
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setAccuracy(accuracy);
        return newLocation;
    }
    
    @LargeTest
    public void testCanSetListener() throws InterruptedException {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the gps location provider.
                SRLOG.v(TAG, "new location is "+location.toString());
                SRLOG.v(TAG, "New location came from: "+location.getProvider());   
                mNewTestLocation = location;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
                SRLOG.v(TAG, "Status Changed " + provider);
            }
    
            public void onProviderEnabled(String provider) {
                SRLOG.v(TAG, "Provider enabled changed");
            }
    
            public void onProviderDisabled(String provider) {
                SRLOG.v(TAG, "Provider disabled changed");
            }
        };
        
        HandlerThread thread = new HandlerThread("LocationTestHandlerThread");
        thread.start(); // starts the thread.
        Looper mLooper = thread.getLooper();
               
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener, mLooper);
        
        Thread.sleep(60000);
        
        Assert.assertTrue("No new location", mNewTestLocation != null);
    }
    
    @SmallTest
    public void testHasGPSProvider() {
        Assert.assertTrue("Device has no GPS", mLocation.deviceHasGPS());
        Assert.assertTrue("Device GPS not enabled", mLocation.isGPSEnabled());
    }
    
    @SmallTest
    public void testCanGetLocation() {
        Location fastLoc = mLocation.getLastPassiveLocation();
        Assert.assertTrue("last location null", fastLoc != null);
        Log.v(TAG, "Lat: "+fastLoc.getLatitude());
        Log.v(TAG, "Long: "+fastLoc.getLongitude());
    }
    
    @SmallTest
    public void testCanMockDirectLocation() throws InterruptedException {
        
        addMockProvider();

        Mountain m = Mountains.getInstance(getContext()).getMountain("Longs Peak");

        Location location = new Location(mockProvider);
        location.setLatitude(m.getLatitude());
        location.setLongitude(m.getLongitude());
        location.setAltitude((double) m.getElevation());
        location.setAccuracy((float)1.123);
        location.setBearing((float)180.0);
        location.setElapsedRealtimeNanos(1);
        location.setTime(System.currentTimeMillis());
                
        setMockLocation(location);
        
        Thread.sleep(1000);

        Location mockLocation = mLocation.getLastMockLocation();
        
        removeMockProvider();
        
        Assert.assertFalse("Location is null", mockLocation == null);
        Assert.assertTrue("Long not correct", m.getLongitude() == mockLocation.getLongitude());
        Assert.assertTrue("Lat not correct", m.getLatitude() == mockLocation.getLatitude());
        Assert.assertTrue("Altitude not correct", (double) m.getElevation() == mockLocation.getAltitude());

    }
    
    @LargeTest
    public void testCanEnableGPSUpdates() throws InterruptedException {
        mLocation.getGPSUpdates();
                
        Thread.sleep(60000);
        
        mLocation.stopUpdates();
        
        Assert.assertTrue("GPS location null", mLocation.getLastUpdateLocation() != null);
        Assert.assertTrue("Type not gps", mLocation.getLastLocationType() == DeviceLocation.LocationType.GPS);
    }
    
    @LargeTest
    public void testCanEnableNetworkUpdates() throws InterruptedException {
        mLocation.getNetworkUpdates();
        
        Thread.sleep(60000);
        
        mLocation.stopUpdates();
        
        Assert.assertTrue("Network location null", mLocation.getLastUpdateLocation() != null);
        Assert.assertTrue("Type not network", mLocation.getLastLocationType() == DeviceLocation.LocationType.NETWORK);
    }
    
    @LargeTest
    public void testCanEnablePassiveUpdates() throws InterruptedException {
        mLocation.getPassiveUpdates();
        
        Thread.sleep(60000);
        
        mLocation.stopUpdates();
        
        Assert.assertTrue("Passive location null", mLocation.getLastUpdateLocation() != null);
    }
    
    @SmallTest
    public void testCanMockListenerLocation() throws InterruptedException {
        
        addMockProvider();

        Mountain m = Mountains.getInstance(getContext()).getMountain("Longs Peak");

        Location location = new Location(mockProvider);
        location.setLatitude(m.getLatitude());
        location.setLongitude(m.getLongitude());
        location.setAltitude((double) m.getElevation());
        location.setAccuracy((float)1.123);
        location.setBearing((float)180.0);
        location.setElapsedRealtimeNanos(1);
        location.setTime(System.currentTimeMillis());
                
        setMockLocation(location);
        
        Thread.sleep(1000);

        Location mockLocation = mLocation.getLastUpdateLocation();
        
        removeMockProvider();
        
        Assert.assertFalse("Location is null", mockLocation == null);
        Assert.assertTrue("Type not mock", mLocation.getLastLocationType() == DeviceLocation.LocationType.MOCK);
        Assert.assertTrue("Long not correct", m.getLongitude() == mockLocation.getLongitude());
        Assert.assertTrue("Lat not correct", m.getLatitude() == mockLocation.getLatitude());
        Assert.assertTrue("Altitude not correct", (double) m.getElevation() == mockLocation.getAltitude());

    }
    
    @MediumTest
    public void testNearestFromDenver() throws InterruptedException {
        Location denver = new Location(mockProvider);
        denver.setLatitude(39.738494);
        denver.setLongitude(-104.9878033);
        denver.setAltitude((double) 5280);
        denver.setAccuracy((float)1.0);
        denver.setBearing((float)180.0);
        denver.setElapsedRealtimeNanos(1);
        denver.setTime(System.currentTimeMillis());

        addMockProvider();

        setMockLocation(denver);
        
        Thread.sleep(1000);
        
        LinkedList<Mountain> nearDenver = mLocation.getNearestMountains(5);
        
        removeMockProvider();
        
        for(Mountain m : nearDenver) {
            SRLOG.v(TAG, m.getName());
        }
        
        Assert.assertTrue("size not 5", nearDenver.size() == 5);
        Assert.assertTrue("nearest not Evans", nearDenver.get(0).getName().equals("Mt. Evans"));
        Assert.assertTrue("nearest not Bierstady", nearDenver.get(1).getName().equals("Mt. Bierstadt"));
        Assert.assertTrue("nearest not Grays", nearDenver.get(2).getName().equals("Grays Peak"));
        Assert.assertTrue("nearest not Torreys", nearDenver.get(3).getName().equals("Torreys Peak"));
        Assert.assertTrue("nearest not Longs", nearDenver.get(4).getName().equals("Longs Peak"));
    }
    
    @MediumTest
    public void testNearestFromVail() throws InterruptedException {
        Location vail = new Location(mockProvider);
        vail.setLatitude(39.606144);
        vail.setLongitude(-106.354972);
        vail.setAltitude((double) 8150);
        vail.setAccuracy((float)1.0);
        vail.setBearing((float)180.0);
        vail.setElapsedRealtimeNanos(1);
        vail.setTime(System.currentTimeMillis());

        addMockProvider();

        setMockLocation(vail);
        
        Thread.sleep(1000);
        
        LinkedList<Mountain> nearVail = mLocation.getNearestMountains(10);
        
        removeMockProvider();
        
        for(Mountain m : nearVail) {
            SRLOG.v(TAG, m.getName());
        }
        
        Assert.assertTrue("size not 10", nearVail.size() == 10);
        Assert.assertTrue("nearest not Holy Cross", nearVail.get(0).getName().equals("Mt. of the Holy Cross"));
        Assert.assertTrue("nearest not Quandary", nearVail.get(1).getName().equals("Quandary Peak"));
        Assert.assertTrue("nearest not Democrat", nearVail.get(2).getName().equals("Mt. Democrat"));
        Assert.assertTrue("nearest not Lincoln", nearVail.get(3).getName().equals("Mt. Lincoln"));
        Assert.assertTrue("nearest not Cameron", nearVail.get(4).getName().equals("Mt. Cameron"));
    }
}