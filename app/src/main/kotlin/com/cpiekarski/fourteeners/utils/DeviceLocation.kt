package com.cpiekarski.fourteeners.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.HandlerThread
import android.os.Looper
import androidx.core.app.ActivityCompat
import java.util.*

/**
 * Determines device location, when to update the location, and which mountains are nearby.
 * 
 * Can be unit tested using the MOCK_PROVIDER location provider.
 */
class DeviceLocation(private val context: Context) {
    companion object {
        const val TAG = "DeviceLocation"
        const val MOCK_PROVIDER = LocationManager.GPS_PROVIDER + "Test"
        const val MIN_UPDATE_TIME = 0L
        const val GPS = 0
        const val NETWORK = 1
        const val MOCK = 2
    }
    
    enum class LocationType {
        GPS, NETWORK, MOCK
    }
    
    private val locationManager: LocationManager
    private val locationListener: LocationListener
    
    private var lastMockLocation: Location? = null
    private var lastGPSLocation: Location? = null
    private var lastNetworkLocation: Location? = null
    private var lastLocation: Location? = null
    private var locationType: LocationType? = null
    private var looper: Looper? = null
    
    init {
        // Acquire a reference to the system Location Manager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Check for permission before accessing last known location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }

        // Define a listener that responds to location updates
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Called when a new location is found by the location provider.
                SRLOG.v(TAG, "new location is ${location}")
                SRLOG.v(TAG, "New location came from: ${location.provider}")
                
                synchronized(this) {
                    when (location.provider) {
                        LocationManager.GPS_PROVIDER -> {
                            lastGPSLocation = location
                            locationType = LocationType.GPS
                        }
                        LocationManager.NETWORK_PROVIDER -> {
                            lastNetworkLocation = location
                            locationType = LocationType.NETWORK
                        }
                        else -> {
                            if (location.isFromMockProvider) {
                                lastMockLocation = location
                                locationType = LocationType.MOCK
                            }
                        }
                    }
                    
                    lastLocation = location
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                SRLOG.v(TAG, "Status Changed $provider")
            }

            override fun onProviderEnabled(provider: String) {
                SRLOG.v(TAG, "Provider enabled changed")
            }

            override fun onProviderDisabled(provider: String) {
                SRLOG.v(TAG, "Provider disabled changed")
            }
        }
        
        val mine = Looper.getMainLooper()
        SRLOG.v(TAG, "looper is $mine")
        
        val thread = HandlerThread("DeviceLocationHandlerThread")
        thread.start() // starts the thread.
        looper = thread.looper
        
        SRLOG.v(TAG, "new looper is $looper")
    }
    
    fun getNetworkUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener, looper)
        }
    }
    
    fun getGPSUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener, looper)
        }
    }
    
    fun getPassiveUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, locationListener, looper)
        }
    }
    
    fun getMockUpdates() {
        // Mock updates for testing - implementation would depend on testing framework
    }
    
    fun getLastGPSLocation(): Location? = lastGPSLocation
    
    fun getLastMockLocation(): Location? = lastMockLocation
    
    @Synchronized
    fun getLastUpdateLocation(): Location? = lastLocation
    
    @Synchronized
    fun getLastLocationType(): LocationType? = locationType
    
    fun getLastPassiveLocation(): Location? {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                   ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } else {
            null
        }
    }
    
    /**
     * Get the nearest mountains to the current location
     * @param howMany maximum number of mountains to return
     * @return ArrayList of nearest mountains, sorted by distance
     */
    fun getNearestMountains(howMany: Int): ArrayList<Mountain> {
        val mountains = Mountains.getInstance(context)
        val allPeakNames = mountains.getAllPeakNames()
        val nearestMountains = ArrayList<Mountain>()
        
        val currentLocation = getLastUpdateLocation() ?: getLastPassiveLocation()
        
        if (currentLocation == null) {
            // If no location available, return first few mountains as fallback
            for (i in 0 until minOf(howMany, allPeakNames.size)) {
                mountains.getMountain(allPeakNames[i])?.let { nearestMountains.add(it) }
            }
            return nearestMountains
        }
        
        // Calculate distances and sort
        val mountainDistances = mutableListOf<Pair<Mountain, Float>>()
        
        for (peakName in allPeakNames) {
            val mountain = mountains.getMountain(peakName)
            if (mountain != null) {
                val mountainLocation = Location("").apply {
                    latitude = mountain.getLatitude()
                    longitude = mountain.getLongitude()
                }
                val distance = currentLocation.distanceTo(mountainLocation)
                mountainDistances.add(Pair(mountain, distance))
            }
        }
        
        // Sort by distance and take the nearest ones
        mountainDistances.sortBy { it.second }
        
        for (i in 0 until minOf(howMany, mountainDistances.size)) {
            nearestMountains.add(mountainDistances[i].first)
        }
        
        return nearestMountains
    }
}