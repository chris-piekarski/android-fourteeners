package com.cpiekarski.fourteeners.test

import android.test.AndroidTestCase
import android.test.suitebuilder.annotation.SmallTest
import com.cpiekarski.fourteeners.utils.Mountain
import com.cpiekarski.fourteeners.utils.Mountains
import junit.framework.Assert

class MountainsTest : AndroidTestCase() {
    private lateinit var mountains: Mountains
    private val tag = "SummitMountainsTest"

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        mountains = Mountains.getInstance(context)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
    }

    fun testSize() {
        Assert.assertTrue("Mountains size not 53", 53 == mountains.getSize())
    }

    @SmallTest
    fun testLongs() {
        val m = mountains.getMountain("Longs Peak")
        Assert.assertTrue("Elevation", 14255 == m?.getElevation())
        Assert.assertTrue("Range", "Front" == m?.getRange())
        Assert.assertTrue("County", "Boulder" == m?.getCounty())
    }

    @SmallTest
    fun testBlanca() {
        val m = mountains.getMountain("Blanca Peak")
        Assert.assertTrue("Elevation", 14345 == m?.getElevation())
        Assert.assertTrue("Range", "Sangre de Cristo" == m?.getRange())
        Assert.assertTrue("County", "Alamosa, Huerfano, Costilla" == m?.getCounty())
    }

    @SmallTest
    fun testSingleton() {
        val m = Mountains.getInstance(context).getMountain("Longs Peak")
        Assert.assertTrue("Elevation", 14255 == m?.getElevation())
        Assert.assertTrue("Range", "Front" == m?.getRange())
        Assert.assertTrue("County", "Boulder" == m?.getCounty())
    }

    @SmallTest
    fun testRanges() {
        val ranges = mountains.getRanges()
        Assert.assertTrue("Ranges not 7", 7 == ranges.size)
    }

    @SmallTest
    fun testFrontRange() {
        val front = mountains.getNamesInRange("Front")
        Assert.assertTrue("Front range not 6", 6 == front?.size)
    }

    @SmallTest
    fun testElkRange() {
        val elk = mountains.getNamesInRange("Elk")
        Assert.assertTrue("Elk range not 7", 7 == elk?.size)
    }

    @SmallTest
    fun testSawatchRange() {
        val sawatch = mountains.getNamesInRange("Sawatch")
        Assert.assertTrue("Sawatch range not 15", 15 == sawatch?.size)
    }
}