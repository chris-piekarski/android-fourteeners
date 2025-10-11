package com.cpiekarski.fourteeners.register

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.cpiekarski.fourteeners.utils.Mountain
import com.cpiekarski.fourteeners.utils.Mountains
import com.cpiekarski.fourteeners.utils.SRLOG

/**
 * CRUD model for a register entry
 */
class RegisterEntry(private val context: Context, entryId: Int = -1) {
    companion object {
        const val NO_VALUE = -1
    }
    
    private val tag = "RegisterEntry"
    
    private var mountain: Mountain? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var distance: String? = null
    
    private var startElevation = NO_VALUE
    private var endElevation = NO_VALUE
    private var peekElevation = NO_VALUE
    private var summit = NO_VALUE
    
    private var startLatLoc = NO_VALUE.toDouble()
    private var startLongLoc = NO_VALUE.toDouble()
    private var endLatLoc = NO_VALUE.toDouble()
    private var endLongLoc = NO_VALUE.toDouble()
    
    private var endLocAccuracy = NO_VALUE
    private var startLocAccuracy = NO_VALUE
    
    private var notes: String? = null
    private var rowId: Long = 0
    private var proof: String? = null
    
    init {
        if (entryId != -1) {
            readEntry(entryId)
        }
    }
    
    private fun getStartLocationTuple(): String {
        return "$startLatLoc;$startLongLoc;$startLocAccuracy"
    }
    
    private fun getEndLocationTuple(): String {
        return "$endLatLoc;$endLongLoc;$endLocAccuracy"
    }
    
    private fun parseStartLocationTuple(tuple: String) {
        val parts = tuple.split(";")
        startLatLoc = parts[0].toDouble()
        startLongLoc = parts[1].toDouble()
        startLocAccuracy = parts[2].toInt()
    }
    
    private fun parseEndLocationTuple(tuple: String) {
        val parts = tuple.split(";")
        endLatLoc = parts[0].toDouble()
        endLongLoc = parts[1].toDouble()
        endLocAccuracy = parts[2].toInt()
    }
    
    /**
     * @return false if failure, true otherwise
     */
    fun createEntry(): Boolean {
        if (mountain == null) {
            SRLOG.w(tag, "No mountain set for entry; entry not created.")
            return false
        }
        
        val r = RegisterHelper(context)
        val d = r.writableDatabase
        val values = ContentValues()
        
        values.put(RegisterHelper.MNT_NAME, getMountainName())
        values.put(RegisterHelper.MNT_RANGE, getMountainRange())
        values.put(RegisterHelper.START_ELEVATION, startElevation)
        values.put(RegisterHelper.START_TIME, startTime)
        values.put(RegisterHelper.START_LOC, getStartLocationTuple())
        values.put(RegisterHelper.END_ELEVATION, endElevation)
        values.put(RegisterHelper.END_TIME, endTime)
        values.put(RegisterHelper.END_LOC, getEndLocationTuple())
        values.put(RegisterHelper.DISTANCE, distance)
        values.put(RegisterHelper.SUMMIT, summit)
        values.put(RegisterHelper.NOTES, notes)
        values.put(RegisterHelper.PROOF, proof)
        values.put(RegisterHelper.PEEK_ELEVATION, peekElevation)
        
        rowId = d.insert(RegisterHelper.TABLE_NAME, null, values)
        SRLOG.i(tag, "Summit register entry created with id: $rowId")
        
        return rowId != -1L
    }
    
    private fun readEntry(entryId: Int) {
        // Implementation for reading entry from database
        // TODO: Implement this method
    }
    
    fun getMountainName(): String? {
        return mountain?.getName()
    }
    
    fun getMountainRange(): String? {
        return mountain?.getRange()
    }
    
    // Getters and setters for other properties
    fun setMountain(mountain: Mountain) {
        this.mountain = mountain
    }
    
    fun setStartTime(startTime: String) {
        this.startTime = startTime
    }
    
    fun setEndTime(endTime: String) {
        this.endTime = endTime
    }
    
    fun setSummit(summit: Int) {
        this.summit = summit
    }
    
    fun setNotes(notes: String) {
        this.notes = notes
    }
    
    fun setProof(proof: String) {
        this.proof = proof
    }
    
    fun setDistance(distance: String) {
        this.distance = distance
    }
}