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
        if (parts.size != 3) return
        // Parse all fields to local variables first; only assign if every field succeeds
        val lat: Double
        val lon: Double
        val acc: Int
        try {
            lat = parts[0].toDouble()
            lon = parts[1].toDouble()
            acc = parts[2].toInt()
        } catch (e: NumberFormatException) {
            return // bad tuple — leave all fields unset
        }
        startLatLoc = lat
        startLongLoc = lon
        startLocAccuracy = acc
    }
    
    private fun parseEndLocationTuple(tuple: String) {
        val parts = tuple.split(";")
        if (parts.size != 3) return
        // Parse all fields to local variables first; only assign if every field succeeds
        val lat: Double
        val lon: Double
        val acc: Int
        try {
            lat = parts[0].toDouble()
            lon = parts[1].toDouble()
            acc = parts[2].toInt()
        } catch (e: NumberFormatException) {
            return // bad tuple — leave all fields unset
        }
        endLatLoc = lat
        endLongLoc = lon
        endLocAccuracy = acc
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
        val r = RegisterHelper(context)
        val db = r.readableDatabase
        val cursor = db.query(
            RegisterHelper.TABLE_NAME,
            null,
            "${RegisterHelper.COLUMN_ID} = ?",
            arrayOf(entryId.toString()),
            null, null, null
        )
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val mntName = cursor.getString(cursor.getColumnIndexOrThrow(RegisterHelper.MNT_NAME))
                val mntRange = cursor.getString(cursor.getColumnIndexOrThrow(RegisterHelper.MNT_RANGE))
                val mts = Mountains.getInstance(context)
                this.mountain = mts.getMountain(mntName, mntRange)

                val startLocIdx = cursor.getColumnIndexOrThrow(RegisterHelper.START_LOC)
                val startLocVal = cursor.getString(startLocIdx)
                if (startLocVal != null) {
                    parseStartLocationTuple(startLocVal)
                }
                val endLocIdx = cursor.getColumnIndexOrThrow(RegisterHelper.END_LOC)
                val endLocVal = cursor.getString(endLocIdx)
                if (endLocVal != null) {
                    parseEndLocationTuple(endLocVal)
                }

                val startTimeIdx = cursor.getColumnIndexOrThrow(RegisterHelper.START_TIME)
                val startTimeVal = cursor.getString(startTimeIdx)
                if (startTimeVal != null) setStartTime(startTimeVal)

                val endTimeIdx = cursor.getColumnIndexOrThrow(RegisterHelper.END_TIME)
                val endTimeVal = cursor.getString(endTimeIdx)
                if (endTimeVal != null) setEndTime(endTimeVal)

                val distanceIdx = cursor.getColumnIndexOrThrow(RegisterHelper.DISTANCE)
                val distanceVal = cursor.getString(distanceIdx)
                if (distanceVal != null) setDistance(distanceVal)

                val summitIdx = cursor.getColumnIndexOrThrow(RegisterHelper.SUMMIT)
                val summitVal = cursor.getString(summitIdx)
                setSummit(if (summitVal != null) summitVal.toInt() else NO_VALUE)

                val startElevIdx = cursor.getColumnIndexOrThrow(RegisterHelper.START_ELEVATION)
                val startElevVal = cursor.getString(startElevIdx)
                startElevation = if (startElevVal != null) startElevVal.toInt() else NO_VALUE

                val endElevIdx = cursor.getColumnIndexOrThrow(RegisterHelper.END_ELEVATION)
                val endElevVal = cursor.getString(endElevIdx)
                endElevation = if (endElevVal != null) endElevVal.toInt() else NO_VALUE

                val peakElevIdx = cursor.getColumnIndexOrThrow(RegisterHelper.PEEK_ELEVATION)
                val peakElevVal = cursor.getString(peakElevIdx)
                peekElevation = if (peakElevVal != null) peakElevVal.toInt() else NO_VALUE

                val notesIdx = cursor.getColumnIndexOrThrow(RegisterHelper.NOTES)
                val notesVal = cursor.getString(notesIdx)
                if (notesVal != null) setNotes(notesVal)

                val proofIdx = cursor.getColumnIndexOrThrow(RegisterHelper.PROOF)
                val proofVal = cursor.getString(proofIdx)
                if (proofVal != null) setProof(proofVal)

                rowId = cursor.getLong(cursor.getColumnIndexOrThrow(RegisterHelper.COLUMN_ID))
            }
        } finally {
            cursor.close()
        }
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

    fun getStartTime(): String? = startTime
    fun getEndTime(): String? = endTime
    fun getNotes(): String? = notes
    fun getProof(): String? = proof
    fun getDistance(): String? = distance
    fun getSummit(): Int = summit
    fun getStartElevation(): Int = startElevation
    fun getEndElevation(): Int = endElevation
    fun getPeekElevation(): Int = peekElevation
    fun getStartLatitude(): Double = startLatLoc
    fun getStartLongitude(): Double = startLongLoc
    fun getEndLatitude(): Double = endLatLoc
    fun getEndLongitude(): Double = endLongLoc
    fun getStartAccuracy(): Int = startLocAccuracy
    fun getEndAccuracy(): Int = endLocAccuracy
    fun getRowId(): Long = rowId
}