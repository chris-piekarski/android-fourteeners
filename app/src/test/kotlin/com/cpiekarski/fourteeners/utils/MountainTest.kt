package com.cpiekarski.fourteeners.utils

import org.junit.Assert.*
import org.junit.Test

class MountainTest {

    @Test
    fun `constructor sets all fields`() {
        val elbert = Mountain(
            name = "Mt. Elbert",
            range = "Sawatch",
            county = "Lake",
            longitude = -106.445417,
            latitude = 39.11807,
            rank = 1,
            elevation = 14433
        )
        assertEquals("Mt. Elbert", elbert.getName())
        assertEquals("Sawatch", elbert.getRange())
        assertEquals("Lake", elbert.getCounty())
        assertEquals(-106.445417, elbert.getLongitude(), 0.000001)
        assertEquals(39.11807, elbert.getLatitude(), 0.000001)
        assertEquals(14433, elbert.getElevation())
    }

    @Test
    fun `equality is true for same field values`() {
        val a = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        val b = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        assertEquals(a, b)
    }

    @Test
    fun `equality is false for different elevation`() {
        val a = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        val b = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 2, 14000)
        assertNotEquals(a, b)
    }

    @Test
    fun `hashCode is same for equal mountains`() {
        val a = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        val b = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `copy changes elevation`() {
        val elbert = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        val copied = elbert.copy(elevation = 14000)
        assertEquals("Mt. Elbert", copied.getName())
        assertEquals(14000, copied.getElevation())
        assertNotEquals(elbert, copied)
    }

    @Test
    fun `copy with no changes produces equal mountain`() {
        val elbert = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        val copied = elbert.copy()
        assertEquals(elbert, copied)
    }

    @Test
    fun `toString returns exact format`() {
        val elbert = Mountain("Mt. Elbert", "Sawatch", "Lake", -106.445, 39.118, 1, 14433)
        assertEquals("Name: Mt. Elbert Range: Sawatch Elevation: 14433", elbert.toString())
    }
}
