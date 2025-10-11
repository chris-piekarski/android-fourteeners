package com.cpiekarski.fourteeners.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Determines device location, when to update the location, and which mountains are nearby.
 * 
 * Can be unit tested using the {@link #MOCK_PROVIDER} location provider.
 *
 */
public class DeviceLocation {
    final public String TAG = "DeviceLocation";

    /**
     * Mock location provider to be used for Unit testing module / system
     */
    static public final String MOCK_PROVIDER = LocationManager.GPS_PROVIDER+"Test";
    
    /**
     * Only take a GPS update every 1000ms
     */
    static public final long MIN_UPDATE_TIME = 0;
    
    static public final int GPS = 0;
    static public final int NETWORK = 1;
    static public final int MOCK = 2;
    
    public enum LocationType {
        GPS, NETWORK, MOCK;
    }
    private Context mCtx;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    
    private Location mLastMockLocation;
    private Location mLastGPSLocation;
    private Location mLastNetworkLocation;
    private Location mLastLocation;
    private LocationType mType;
    private Looper mLooper;
    
    public DeviceLocation(Context ctx) {
        mCtx = ctx;
        
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);
        if (hasLocationPermission()) {
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the gps location provider.
                SRLOG.v(TAG, "new location is "+location.toString());
                SRLOG.v(TAG, "New location came from: "+location.getProvider());
                
                synchronized(this) {
                    if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                        mLastGPSLocation = location;
                        mType = LocationType.GPS;
                    } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                        mLastNetworkLocation = location;
                        mType = LocationType.NETWORK;
                    } else if (location.isFromMockProvider()) {
                        mLastMockLocation = location;
                        mType = LocationType.MOCK;
                    }
                    
                    mLastLocation = location;
                }
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
        
        Looper mine = Looper.getMainLooper();
        SRLOG.v(TAG, "looper is "+mine);
        
        HandlerThread thread = new HandlerThread("DeviceLocationHandlerThread");
        thread.start(); // starts the thread.
        mLooper = thread.getLooper();
        
        SRLOG.v(TAG, "new looper is "+mLooper);
        
        // Register the listener with the Location Manager to receive location updates
        //mLocationManager.requestLocationUpdates(MOCK_PROVIDER, 0, 0, mLocationListener);

    }

    private boolean hasLocationPermission() {
        int fine = ContextCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED;
    }
    
    public void dumpProviders() {
        for(String l : mLocationManager.getAllProviders()) {
            SRLOG.v(TAG, l);
        }
    }
    
    /**
     * Start updates from the Network provider only
     */
    public void getNetworkUpdates() {
        if (!hasLocationPermission()) {
            SRLOG.w(TAG, "Location permission not granted; skipping network updates.");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener, mLooper);
    }
    
    /**
     * Start updates from only the GPS provider only
     */
    public void getGPSUpdates() {
        if (!hasLocationPermission()) {
            SRLOG.w(TAG, "Location permission not granted; skipping GPS updates.");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, 0, mLocationListener, mLooper);
    }
    
    /**
     * Start updates from any provider on the system
     */
    public void getPassiveUpdates() {
        if (!hasLocationPermission()) {
            SRLOG.w(TAG, "Location permission not granted; skipping passive updates.");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mLocationListener, mLooper);
    }
    
    /**
     * Get a location from only the {@link #MOCK_PROVIDER} provider
     */
    public void getMockUpdates() {
        SRLOG.v(TAG, "Requesting location updates for mock provider");
        mLocationManager.requestLocationUpdates(MOCK_PROVIDER, 0, 0, mLocationListener, mLooper);
    }
    
    /**
     * Remove the location listener from the {@link #mLocationManager}
     */
    public void stopUpdates() {
        mLocationManager.removeUpdates(mLocationListener);
    }
    
    /**
     * Polls LocationManager for last known location.
     * 
     * @return last know location directly from LocationManager
     */
    public Location getLastGPSLocation() {
        if (!hasLocationPermission()) {
            SRLOG.w(TAG, "Location permission not granted; last GPS location unavailable.");
            return null;
        }
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    
    /**
     * Polls LocationManager for last mocked location 
     * @see {@link #MOCK_PROVIDER}
     * 
     * @return Null if never set, Location otherwise
     */
    public Location getLastMockLocation() {
        if (!hasLocationPermission()) {
            SRLOG.w(TAG, "Location permission not granted; last mock location unavailable.");
            return null;
        }
        return mLocationManager.getLastKnownLocation(MOCK_PROVIDER);
    }
    
    /**
     * Returns the last known location from the receiver
     * 
     * @return Null if never set, Location otherwise
     */
    public synchronized Location getLastUpdateLocation() {
        return mLastLocation;
    }
    
    public synchronized LocationType getLastLocationType() {
        return mType;
    }
    
    public Location getLastPassiveLocation() {
        if (!hasLocationPermission()) {
            SRLOG.w(TAG, "Location permission not granted; last passive location unavailable.");
            return null;
        }
        return mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }
    
    /**
     * Indicates if the device has a GPS provider
     * 
     * @return True if the GPS providers exists
     */
    public boolean deviceHasGPS() {
        List<String> providers = mLocationManager.getAllProviders();
        if(providers.contains(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }
    
    /**
     * Is the GPS provider enabled on the device?
     * 
     * @return True if enabled
     */
    public boolean isGPSEnabled() {
        List<String> enabled = mLocationManager.getProviders(true);
        if(enabled.contains(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }
    
    /**
     * Given our last received location, provide a ArrayList of Mountains with max
     * size of howMany.
     * 
     * O(n log n) where N is the number of mountains
     * 
     * @param howMany Maximum number of nearest Mountains to return.
     * @return sorted list of nearest Mountains
     */
    public final ArrayList<Mountain> getNearestMountains(int howMany) {
        ArrayList<Mountain> l = new ArrayList<Mountain>();
        final Location loc = mLastLocation;

        TreeSet<Mountain> mntTree = new TreeSet<Mountain>(new Comparator<Mountain>() {

            /**
             * an integer < 0 if lhs is less than rhs, 0 if they are equal, and > 0 if lhs is greater than rhs.
             */
            @Override
            public int compare(Mountain lhs, Mountain rhs) {
                float[] r1 = new float[1];
                float[] r2 = new float[1];
                SRLOG.v(TAG, loc.toString());
                SRLOG.v(TAG, lhs.toString());
                SRLOG.v(TAG, rhs.toString());
                Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), lhs.getLatitude(), lhs.getLongitude(), r1);
                Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), rhs.getLatitude(), rhs.getLongitude(), r2);
                return r1[0] < r2[0] ? -1 : r1[0] == r2[0] ? 0 : 1;
            }
        });
        
        SRLOG.d(TAG, "Starting add "+loc.toString());
        //add all mountains to the sorted tree array
        Mountains mnts = Mountains.getInstance(mCtx);
        for(String name: mnts.getAllPeakNames()) {
            SRLOG.i(TAG, "Adding "+name);
            mntTree.add(mnts.getMountain(name));
        }
        
        int i = 0;
        for(Mountain m : mntTree) {
            if(i < howMany) {
                SRLOG.i(TAG, "Adding "+m.getName());
                l.add(m);
                ++i;
            } else {
                break;
            }
        }
        
        return l;
    }
}