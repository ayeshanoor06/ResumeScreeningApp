package com.ayesha.resumescreeningapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    @Insert
    suspend fun insertCandidate(
        candidate: CandidateEntity
    )

    @Query("SELECT * FROM candidates ORDER BY id DESC")
    fun getAllCandidates(): Flow<List<CandidateEntity>>

    @Update
    suspend fun updateCandidate(
        candidate: CandidateEntity
    )

    @Delete
    suspend fun deleteCandidate(
        candidate: CandidateEntity
    )
}