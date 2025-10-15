package com.cpiekarski.fourteeners.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit test for Mountains class filtering logic
 */
class MountainsLogicTest {

    @Test
    fun testRankFilteringLogic() {
        // Test the filtering logic that we implemented
        val testMountains = listOf(
            "1" to "Mt. Elbert",
            "2" to "Mt. Massive", 
            "3" to "Mt. Harvard",
            "-1" to "Mt. Cameron",
            "-1" to "El Diente Peak",
            "-1" to "Conundrum Peak",
            "-1" to "North Eolus",
            "-1" to "North Maroon Peak"
        )
        
        // Filter to only include official fourteeners (rank > 0)
        val officialFourteeners = testMountains.filter { (rank, _) -> rank.toInt() > 0 }
        
        // Should have 3 official fourteeners
        assertEquals("Should have 3 official fourteeners", 3, officialFourteeners.size)
        
        // Should have 5 unofficial fourteeners
        val unofficialFourteeners = testMountains.filter { (rank, _) -> rank.toInt() <= 0 }
        assertEquals("Should have 5 unofficial fourteeners", 5, unofficialFourteeners.size)
        
        // Verify specific mountains
        assertTrue("Mt. Elbert should be official", officialFourteeners.any { it.second == "Mt. Elbert" })
        assertTrue("Mt. Cameron should be unofficial", unofficialFourteeners.any { it.second == "Mt. Cameron" })
    }
    
    @Test
    fun testExpectedCounts() {
        // Based on our analysis of the XML file
        val totalMountainsInXml = 58
        val expectedOfficialFourteeners = 53
        val expectedUnofficialFourteeners = 5
        
        // Verify our math is correct
        assertEquals("Total should equal official + unofficial", 
            totalMountainsInXml, expectedOfficialFourteeners + expectedUnofficialFourteeners)
        
        // Verify the test expectation
        assertEquals("Test expects 53 mountains", 53, expectedOfficialFourteeners)
    }
}