package com.cpiekarski.fourteeners.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SummitAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(attempt: SummitAttempt): Long

    @Query("SELECT * FROM summit_attempt ORDER BY startTimeIso DESC")
    fun observeAttempts(): Flow<List<SummitAttempt>>

    @Query("SELECT COUNT(*) FROM summit_attempt WHERE isSummit = 1")
    fun observeTotalSummits(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT mountainName) FROM summit_attempt WHERE isSummit = 1")
    fun observeUniqueSummits(): Flow<Int>
}
