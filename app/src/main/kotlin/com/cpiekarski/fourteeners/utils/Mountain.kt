package com.cpiekarski.fourteeners.utils

data class Mountain(
    private val name: String,
    private val range: String,
    private val county: String,
    private val longitude: Double,
    private val latitude: Double,
    private val rank: Int,
    private val elevation: Int
) {
    companion object {
        const val NONE_OFFICIAL_RANK = -1
    }
    
    fun getLongitude(): Double = longitude
    fun getLatitude(): Double = latitude
    fun getName(): String = name
    fun getRange(): String = range
    fun getCounty(): String = county
    fun getElevation(): Int = elevation
    
    override fun toString(): String {
        return "Name: $name Range: $range Elevation: $elevation"
    }
}