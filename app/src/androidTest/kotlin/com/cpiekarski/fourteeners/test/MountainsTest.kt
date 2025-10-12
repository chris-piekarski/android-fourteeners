package com.cpiekarski.fourteeners.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.cpiekarski.fourteeners.utils.Mountain
import com.cpiekarski.fourteeners.utils.Mountains
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MountainsTest {
    private lateinit var mountains: Mountains
    private val tag = "SummitMountainsTest"

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        mountains = Mountains.getInstance(context)
    }

    @Test
    fun testSize() {
        Assert.assertTrue("Mountains size not 53", 53 == mountains.getSize())
    }

    @Test
    fun testLongs() {
        val m = mountains.getMountain("Longs Peak")
        Assert.assertTrue("Elevation", 14255 == m?.getElevation())
        Assert.assertTrue("Range", "Front" == m?.getRange())
        Assert.assertTrue("County", "Boulder" == m?.getCounty())
    }

    @Test
    fun testBlanca() {
        val m = mountains.getMountain("Blanca Peak")
        Assert.assertTrue("Elevation", 14345 == m?.getElevation())
        Assert.assertTrue("Range", "Sangre de Cristo" == m?.getRange())
        Assert.assertTrue("County", "Alamosa, Huerfano, Costilla" == m?.getCounty())
    }

    @Test
    fun testSingleton() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val m = Mountains.getInstance(context).getMountain("Longs Peak")
        Assert.assertTrue("Elevation", 14255 == m?.getElevation())
        Assert.assertTrue("Range", "Front" == m?.getRange())
        Assert.assertTrue("County", "Boulder" == m?.getCounty())
    }

    @Test
    fun testRanges() {
        val ranges = mountains.getRanges()
        Assert.assertTrue("Ranges not 7", 7 == ranges.size)
    }

    @Test
    fun testFrontRange() {
        val front = mountains.getNamesInRange("Front")
        Assert.assertTrue("Front range not 6", 6 == front?.size)
    }

    @Test
    fun testElkRange() {
        val elk = mountains.getNamesInRange("Elk")
        Assert.assertTrue("Elk range not 7", 7 == elk?.size)
    }

    @Test
    fun testSawatchRange() {
        val sawatch = mountains.getNamesInRange("Sawatch")
        Assert.assertTrue("Sawatch range not 15", 15 == sawatch?.size)
    }
}