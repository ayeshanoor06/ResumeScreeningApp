package com.ayesha.resumescreeningapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val OceanDark = Color(0xFF001F27)
private val OceanTeal = Color(0xFF154E60)
private val OceanBlue = Color(0xFF5D8797)
private val SoftSage = Color(0xFFAFC2B2)
private val LightCream = Color(0xFFEEF4DD)

@Composable
fun AdminPanel(
    database: AppDatabase
) {

    val candidates by database
        .candidateDao()
        .getAllCandidates()
        .collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightCream)
            .padding(16.dp)
    ) {

        Text(
            text = "Admin Panel",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = OceanDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Candidate Review & Shortlisting",
            fontSize = 15.sp,
            color = OceanTeal
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (candidates.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "No candidates found",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Analyze a resume to add a candidate.",
                    fontSize = 14.sp,
                    color = OceanTeal
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                items(
                    items = candidates,
                    key = { it.id }
                ) { candidate ->

                    CandidateCard(
                        candidate = candidate,
                        onShortlistClick = {

                            val updatedCandidate =
                                candidate.copy(
                                    isShortlisted = !candidate.isShortlisted
                                )

                            coroutineScope.launch(Dispatchers.IO) {

                                database
                                    .candidateDao()
                                    .updateCandidate(updatedCandidate)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CandidateCard(
    candidate: CandidateEntity,
    onShortlistClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = candidate.fileName,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = OceanDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Job Role: ${candidate.jobRole}",
                fontSize = 14.sp,
                color = OceanTeal
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ScoreItem(
                    title = "Fit",
                    value = "${candidate.fitScore}%"
                )

                ScoreItem(
                    title = "Keywords",
                    value = "${candidate.keywordScore}%"
                )

                ScoreItem(
                    title = "Sentiment",
                    value = "${candidate.sentimentScore}%"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Sentiment: ${candidate.sentimentLabel}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OceanBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (candidate.matchedKeywords.isNotBlank()) {

                Text(
                    text = "Matched Keywords",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = candidate.matchedKeywords,
                    fontSize = 13.sp,
                    color = OceanTeal
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (candidate.missingKeywords.isNotBlank()) {

                Text(
                    text = "Missing Keywords",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OceanDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = candidate.missingKeywords,
                    fontSize = 13.sp,
                    color = OceanTeal
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            Button(
                onClick = onShortlistClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (candidate.isShortlisted) {
                            OceanBlue
                        } else {
                            OceanTeal
                        }
                )
            ) {

                Text(
                    text =
                        if (candidate.isShortlisted) {
                            "Remove from Shortlist"
                        } else {
                            "Shortlist Candidate"
                        },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text =
                    if (candidate.isShortlisted) {
                        "Status: Shortlisted"
                    } else {
                        "Status: Under Review"
                    },
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color =
                    if (candidate.isShortlisted) {
                        OceanTeal
                    } else {
                        OceanBlue
                    }
            )
        }
    }
}

@Composable
private fun ScoreItem(
    title: String,
    value: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = OceanDark
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            color = OceanBlue
        )
    }
}