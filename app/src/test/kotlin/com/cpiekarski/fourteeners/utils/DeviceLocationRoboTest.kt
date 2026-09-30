package com.cpiekarski.fourteeners.utils

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.cpiekarski.fourteeners.SummitRegister
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(application = SummitRegister::class)
class DeviceLocationRoboTest {

    companion object {
        private val instanceField = Mountains::class.java.getDeclaredField("INSTANCE")
            .apply { isAccessible = true }
        private val listenerField = DeviceLocation::class.java.getDeclaredField("locationListener")
            .apply { isAccessible = true }

        fun resetMountains() {
            instanceField.set(null, null)
        }

        fun invokeOnLocationChanged(listener: Any, location: Location) {
            val method = listener::class.java.getDeclaredMethod("onLocationChanged", Location::class.java)
            method.invoke(listener, location)
        }

        fun invokeOnStatusChanged(listener: Any, provider: String, status: Int, extras: Bundle) {
            val method = listener::class.java.getDeclaredMethod(
                "onStatusChanged", String::class.java, Int::class.javaPrimitiveType, Bundle::class.java
            )
            method.invoke(listener, provider, status, extras)
        }

        fun invokeOnProviderEnabled(listener: Any, provider: String) {
            val method = listener::class.java.getDeclaredMethod("onProviderEnabled", String::class.java)
            method.invoke(listener, provider)
        }

        fun invokeOnProviderDisabled(listener: Any, provider: String) {
            val method = listener::class.java.getDeclaredMethod("onProviderDisabled", String::class.java)
            method.invoke(listener, provider)
        }

        fun getLocationListener(dl: DeviceLocation): Any {
            return listenerField.get(dl)
        }
    }

    private lateinit var deviceLocation: DeviceLocation
    private val context by lazy { RuntimeEnvironment.getApplication() as SummitRegister }

    @Before
    fun setUp() {
        resetMountains()
        ShadowApplication.getInstance().grantPermissions(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        deviceLocation = DeviceLocation(context)
    }

    @After
    fun tearDown() {
        deviceLocation.stopUpdates()
    }

    // Verify that permissions were actually granted
    @Test
    fun `ACCESS_FINE_LOCATION is granted`() {
        assertEquals(
            PackageManager.PERMISSION_GRANTED,
            context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }

    @Test
    fun `ACCESS_COARSE_LOCATION is granted`() {
        assertEquals(
            PackageManager.PERMISSION_GRANTED,
            context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    @Test
    fun `getLastUpdateLocation is null initially`() {
        assertNull(deviceLocation.getLastUpdateLocation())
    }

    @Test
    fun `getLastLocationType is null initially`() {
        assertNull(deviceLocation.getLastLocationType())
    }

    @Test
    fun `getLastGPSLocation is null initially`() {
        assertNull(deviceLocation.getLastGPSLocation())
    }

    @Test
    fun `getLastMockLocation is null initially`() {
        assertNull(deviceLocation.getLastMockLocation())
    }

    @Test
    fun `getGPSUpdates registers listener and triggers GPS update`() {
        deviceLocation.getGPSUpdates()

        val loc = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 39.11807
            longitude = -106.445417
        }

        val listener = getLocationListener(deviceLocation)
        invokeOnLocationChanged(listener, loc)

        val update = deviceLocation.getLastUpdateLocation()
        assertNotNull(update)
        assertEquals(39.11807, update!!.latitude, 0.000001)
        assertEquals(DeviceLocation.LocationType.GPS, deviceLocation.getLastLocationType())
        assertNotNull(deviceLocation.getLastGPSLocation())
    }

    @Test
    fun `getNearestMountains with GPS location near Elbert returns Elbert first`() {
        deviceLocation.getGPSUpdates()

        val loc = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 39.11807
            longitude = -106.445417
        }

        val listener = getLocationListener(deviceLocation)
        invokeOnLocationChanged(listener, loc)

        val nearest = deviceLocation.getNearestMountains(5)
        assertEquals("Mt. Elbert", nearest[0].getName())
        assertEquals(5, nearest.size)
    }

    @Test
    fun `getNearestMountains returns fallback when no location available`() {
        val nearest = deviceLocation.getNearestMountains(5)
        assertEquals(5, nearest.size)
        assertTrue(nearest[0].getName().isNotBlank())
    }

    @Test
    fun `getNearestMountains returns fewer when howMany exceeds total`() {
        val nearest = deviceLocation.getNearestMountains(100)
        assertEquals(53, nearest.size)
    }

    @Test
    fun `getNearestMountains returns empty list when howMany is 0`() {
        val nearest = deviceLocation.getNearestMountains(0)
        assertTrue(nearest.isEmpty())
    }

    @Test
    fun `NETWORK_PROVIDER update sets location type to NETWORK`() {
        deviceLocation.getNetworkUpdates()

        val loc = Location(LocationManager.NETWORK_PROVIDER).apply {
            latitude = 39.0
            longitude = -106.0
        }

        val listener = getLocationListener(deviceLocation)
        invokeOnLocationChanged(listener, loc)

        assertEquals(DeviceLocation.LocationType.NETWORK, deviceLocation.getLastLocationType())
    }

    @Test
    fun `getMockUpdates is a no-op`() {
        deviceLocation.getMockUpdates()
        assertNull(deviceLocation.getLastUpdateLocation())
        assertNull(deviceLocation.getLastLocationType())
    }

    @Test
    fun `PASSIVE_PROVIDER update sets location`() {
        deviceLocation.getPassiveUpdates()

        val loc = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 40.0
            longitude = -107.0
        }

        val listener = getLocationListener(deviceLocation)
        invokeOnLocationChanged(listener, loc)

        assertNotNull(deviceLocation.getLastUpdateLocation())
        assertEquals(40.0, deviceLocation.getLastUpdateLocation()!!.latitude, 0.000001)
    }

    @Test
    fun `custom provider triggers else branch but not MOCK when isFromMockProvider false`() {
        deviceLocation.getGPSUpdates()

        // Use a provider name that's neither GPS nor NETWORK to hit the else branch
        val loc = Location("CustomTestProvider").apply {
            latitude = 40.0
            longitude = -107.0
        }

        val listener = getLocationListener(deviceLocation)
        invokeOnLocationChanged(listener, loc)

        // isFromMockProvider is false, so MOCK should NOT be set
        // but lastLocation should still be captured
        assertNotNull(deviceLocation.getLastUpdateLocation())
        // No specific mock location data should be set
        assertNull(deviceLocation.getLastMockLocation())
        assertNotNull(deviceLocation.getLastUpdateLocation())
    }

    @Test
    fun `stopUpdates stops the HandlerThread`() {
        deviceLocation.stopUpdates()
        // stopUpdates should not throw
        // Subsequent calls should be safe (idempotent)
        deviceLocation.stopUpdates()
    }

    @Test
    fun `onStatusChanged callback is invoked via reflection`() {
        deviceLocation.getGPSUpdates()

        val listener = getLocationListener(deviceLocation)
        val extras = Bundle()
        extras.putString("testKey", "testValue")

        // Should not throw - just logs via SRLOG
        invokeOnStatusChanged(listener, LocationManager.GPS_PROVIDER, 2, extras)
    }

    @Test
    fun `onProviderEnabled callback is invoked via reflection`() {
        deviceLocation.getGPSUpdates()

        val listener = getLocationListener(deviceLocation)

        // Should not throw - just logs via SRLOG
        invokeOnProviderEnabled(listener, LocationManager.GPS_PROVIDER)
    }

    @Test
    fun `onProviderDisabled callback is invoked via reflection`() {
        deviceLocation.getGPSUpdates()

        val listener = getLocationListener(deviceLocation)

        // Should not throw - just logs via SRLOG
        invokeOnProviderDisabled(listener, LocationManager.NETWORK_PROVIDER)
    }
}
