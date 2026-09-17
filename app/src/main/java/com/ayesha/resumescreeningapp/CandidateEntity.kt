package com.ayesha.resumescreeningapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class CandidateEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val fileName: String,

    val jobRole: String,

    val keywordScore: Int,

    val sentimentScore: Int,

    val fitScore: Int,

    val sentimentLabel: String,

    val matchedKeywords: String,

    val missingKeywords: String,

    val isShortlisted: Boolean = false
)