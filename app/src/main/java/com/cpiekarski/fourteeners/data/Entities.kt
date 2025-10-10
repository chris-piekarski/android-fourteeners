package com.cpiekarski.fourteeners.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "summit_attempt")
data class SummitAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mountainName: String,
    val mountainRange: String,
    val startTimeIso: String?,
    val endTimeIso: String?,
    val startLat: Double?,
    val startLon: Double?,
    val endLat: Double?,
    val endLon: Double?,
    val startAcc: Int?,
    val endAcc: Int?,
    val startElevationFt: Int?,
    val endElevationFt: Int?,
    val peakElevationFt: Int?,
    val distanceMiles: String?,
    val notes: String?,
    val proofPath: String?,
    val isSummit: Boolean
)
