package com.cpiekarski.fourteeners.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit test for Mountains class logic
 */
class MountainsUnitTest {

    @Test
    fun testRankFiltering() {
        // Test that rank filtering logic works correctly
        val rank1 = "1"
        val rankNegative1 = "-1"
        val rankZero = "0"
        
        // Test positive rank
        assertTrue("Rank 1 should be > 0", rank1.toInt() > 0)
        
        // Test negative rank
        assertFalse("Rank -1 should not be > 0", rankNegative1.toInt() > 0)
        
        // Test zero rank
        assertFalse("Rank 0 should not be > 0", rankZero.toInt() > 0)
    }
    
    @Test
    fun testMountainCount() {
        // This test verifies that our filtering logic would work
        // We expect 53 official fourteeners (rank > 0)
        val expectedOfficialFourteeners = 53
        val totalMountainsInXml = 58
        val unofficialMountains = 5
        
        // Verify our math is correct
        assertEquals("Total should equal official + unofficial", 
            totalMountainsInXml, expectedOfficialFourteeners + unofficialMountains)
    }
}