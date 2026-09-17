package com.ayesha.resumescreeningapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



// OCEAN SERENITY COLOR PALETTE


private val OceanDark = Color(0xFF001F27)
private val OceanTeal = Color(0xFF154E60)
private val OceanBlue = Color(0xFF5D8797)
private val SoftSage = Color(0xFFAFC2B2)
private val LightCream = Color(0xFFEEF4DD)



// MAIN ACTIVITY


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ResumeScreeningApp()
        }
    }
}



// MAIN APP


@Composable
fun ResumeScreeningApp() {

    val context = LocalContext.current

    val coroutineScope =
        rememberCoroutineScope()


    // ROOM DATABASE


    val database = remember {
        AppDatabase.getDatabase(context)
    }



    // SCREEN STATE


    var showAdminPanel by remember {
        mutableStateOf(false)
    }



    // RESUME STATES


    var selectedFileName by remember {
        mutableStateOf<String?>(null)
    }

    var selectedFileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var resumeText by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var selectedRole by remember {
        mutableStateOf<JobRole?>(null)
    }

    var roleMenuExpanded by remember {
        mutableStateOf(false)
    }

    var analysisResult by remember {
        mutableStateOf<KeywordAnalysisResult?>(null)
    }

    var sentimentResult by remember {
        mutableStateOf<SentimentAnalysisResult?>(null)
    }

    var fitScoreResult by remember {
        mutableStateOf<FitScoreResult?>(null)
    }



    // ADMIN PANEL


    if (showAdminPanel) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightCream)
        ) {

            // BACK BUTTON

            TextButton(
                onClick = {
                    showAdminPanel = false
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            ) {

                Text(
                    text = "← Back to Resume Screening",

                    color = OceanTeal,

                    fontSize = 15.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }


            // ADMIN PANEL

            AdminPanel(
                database = database
            )
        }

        return
    }



    // FILE PICKER


    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            selectedFileUri = uri

            selectedFileName =
                getFileName(
                    context = context,
                    uri = uri
                )

            val fileName =
                selectedFileName
                    ?.lowercase()
                    ?: ""



            analysisResult = null
            sentimentResult = null
            fitScoreResult = null



            // TEXT FILE


            if (fileName.endsWith(".txt")) {

                try {

                    val inputStream =
                        context.contentResolver
                            .openInputStream(uri)

                    resumeText =
                        inputStream
                            ?.bufferedReader()
                            ?.use { reader ->
                                reader.readText()
                            }
                            ?: ""

                    message =
                        if (resumeText.isNotEmpty()) {

                            "Text resume loaded successfully."

                        } else {

                            "The selected text file is empty."
                        }

                } catch (e: Exception) {

                    resumeText = ""

                    message =
                        "Unable to read the selected text file."
                }



                // PDF FILE


            } else if (fileName.endsWith(".pdf")) {

                isLoading = true

                message =
                    "Reading PDF resume..."

                coroutineScope.launch(
                    Dispatchers.IO
                ) {

                    val extractedText =
                        PdfParser.extractText(
                            context = context,
                            uri = uri
                        )

                    withContext(
                        Dispatchers.Main
                    ) {

                        isLoading = false

                        resumeText =
                            extractedText

                        message =
                            if (
                                extractedText.isNotEmpty()
                            ) {

                                "PDF resume parsed successfully."

                            } else {

                                "No readable text was found in this PDF."
                            }
                    }
                }



                // INVALID FILE


            } else {

                message =
                    "Please select a PDF or TXT file."
            }
        }



    // MAIN RESUME SCREEN


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightCream
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightCream)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {



            // LOGO


            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(
                        color = OceanDark,
                        shape =
                            RoundedCornerShape(22.dp)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "RS",

                    color = LightCream,

                    fontSize = 27.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )



            // TITLE


            Text(
                text = "Resume Screening",

                color = OceanDark,

                fontSize = 30.sp,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Text(
                text =
                    "AI-powered resume analysis",

                color = OceanBlue,

                fontSize = 16.sp,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )



            // ADMIN PANEL BUTTON


            Button(
                onClick = {

                    showAdminPanel = true

                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            OceanDark,

                        contentColor =
                            LightCream
                    )
            ) {

                Text(
                    text =
                        "Open Admin Panel",

                    fontSize = 15.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }


            Spacer(
                modifier = Modifier.height(32.dp)
            )



            // UPLOAD RESUME


            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Upload your resume",

                        color = OceanDark,

                        fontSize = 21.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(
                        text =
                            "Select a PDF or text resume to begin analysis.",

                        color = OceanBlue,

                        fontSize = 14.sp,

                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )


                    Button(
                        onClick = {

                            filePickerLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "text/plain"
                                )
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    OceanTeal,

                                contentColor =
                                    Color.White
                            )
                    ) {

                        Text(
                            text =
                                "Upload Resume",

                            fontSize = 16.sp,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    Text(
                        text =
                            "Supported formats: PDF • TXT",

                        color = OceanBlue,

                        fontSize = 13.sp
                    )
                }
            }



            // LOADING


            if (isLoading) {

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                CircularProgressIndicator(
                    color = OceanTeal
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                Text(
                    text =
                        "Extracting resume text...",

                    color = OceanTeal,

                    fontSize = 14.sp
                )
            }



            // SELECTED FILE


            if (selectedFileName != null) {

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                SoftSage
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text =
                                "Selected Resume",

                            color = OceanDark,

                            fontSize = 15.sp,

                            fontWeight =
                                FontWeight.Medium
                        )


                        Spacer(
                            modifier =
                                Modifier.height(7.dp)
                        )


                        Text(
                            text =
                                selectedFileName
                                    ?: "",

                            color = OceanDark,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(
                            text =
                                if (
                                    resumeText.isNotEmpty()
                                ) {

                                    "Text extracted successfully"

                                } else {

                                    "Waiting for text extraction"
                                },

                            color = OceanTeal,

                            fontSize = 13.sp
                        )
                    }
                }
            }



            // MESSAGE


            if (message.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                Text(
                    text = message,

                    color = OceanTeal,

                    fontSize = 13.sp,

                    textAlign =
                        TextAlign.Center
                )
            }



            // JOB ROLE


            if (resumeText.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {

                        Text(
                            text =
                                "Select Job Role",

                            color = OceanDark,

                            fontSize = 20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        Text(
                            text =
                                "Choose the role to compare with the resume.",

                            color = OceanBlue,

                            fontSize = 14.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Button(
                                onClick = {

                                    roleMenuExpanded =
                                        true
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(14.dp),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            OceanDark,

                                        contentColor =
                                            LightCream
                                    )
                            ) {

                                Text(
                                    text =
                                        selectedRole?.name
                                            ?: "Choose Job Role"
                                )
                            }


                            DropdownMenu(
                                expanded =
                                    roleMenuExpanded,

                                onDismissRequest = {

                                    roleMenuExpanded =
                                        false
                                }
                            ) {

                                JobRoles.roles.forEach { role ->

                                    DropdownMenuItem(
                                        text = {

                                            Text(
                                                text =
                                                    role.name
                                            )
                                        },

                                        onClick = {

                                            selectedRole =
                                                role

                                            roleMenuExpanded =
                                                false

                                            analysisResult =
                                                null

                                            sentimentResult =
                                                null

                                            fitScoreResult =
                                                null
                                        }
                                    )
                                }
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )



                        // ANALYZE BUTTON


                        if (selectedRole != null) {

                            Button(
                                onClick = {

                                    val role =
                                        selectedRole

                                    if (role != null) {


                                        // KEYWORD ANALYSIS

                                        val keywordAnalysis =
                                            KeywordAnalyzer.analyze(
                                                resumeText =
                                                    resumeText,

                                                jobRole =
                                                    role
                                            )


                                        // SENTIMENT ANALYSIS

                                        val sentimentAnalysis =
                                            SentimentAnalyzer.analyze(
                                                resumeText =
                                                    resumeText
                                            )


                                        // FIT SCORE

                                        val fitResult =
                                            FitScoreCalculator.calculate(
                                                keywordScore =
                                                    keywordAnalysis
                                                        .percentage,

                                                sentimentScore =
                                                    sentimentAnalysis
                                                        .sentimentScore
                                            )


                                        // SHOW RESULTS

                                        analysisResult =
                                            keywordAnalysis

                                        sentimentResult =
                                            sentimentAnalysis

                                        fitScoreResult =
                                            fitResult



                                        // SAVE CANDIDATE TO ROOM


                                        val candidate =
                                            CandidateEntity(

                                                fileName =
                                                    selectedFileName
                                                        ?: "Unknown Resume",

                                                jobRole =
                                                    role.name,

                                                keywordScore =
                                                    keywordAnalysis
                                                        .percentage,

                                                sentimentScore =
                                                    sentimentAnalysis
                                                        .sentimentScore,

                                                fitScore =
                                                    fitResult
                                                        .finalScore,

                                                sentimentLabel =
                                                    sentimentAnalysis
                                                        .sentimentLabel,

                                                matchedKeywords =
                                                    keywordAnalysis
                                                        .matchedKeywords
                                                        .joinToString(
                                                            ", "
                                                        ),

                                                missingKeywords =
                                                    keywordAnalysis
                                                        .missingKeywords
                                                        .joinToString(
                                                            ", "
                                                        ),

                                                isShortlisted =
                                                    false
                                            )


                                        // Room database operation
                                        // runs on background thread

                                        coroutineScope.launch(
                                            Dispatchers.IO
                                        ) {

                                            database
                                                .candidateDao()
                                                .insertCandidate(
                                                    candidate
                                                )
                                        }


                                        message =
                                            "Resume analysis completed and candidate saved."
                                    }
                                },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),

                                shape =
                                    RoundedCornerShape(14.dp),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            OceanTeal,

                                        contentColor =
                                            Color.White
                                    )
                            ) {

                                Text(
                                    text =
                                        "Analyze Resume",

                                    fontSize = 16.sp,

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }



            // FINAL FIT SCORE


            fitScoreResult?.let { result ->

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                OceanTeal
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                "Overall Fit Percentage",

                            color = LightCream,

                            fontSize = 21.sp,

                            fontWeight =
                                FontWeight.Bold,

                            textAlign =
                                TextAlign.Center
                        )


                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )


                        Text(
                            text =
                                "${result.finalScore}%",

                            color = LightCream,

                            fontSize = 52.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )


                        Text(
                            text =
                                selectedRole?.name ?: "",

                            color = SoftSage,

                            fontSize = 15.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(22.dp)
                        )


                        Text(
                            text =
                                "Keyword Match: ${result.keywordScore}%",

                            color = LightCream,

                            fontSize = 14.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )


                        Text(
                            text =
                                "Sentiment Score: ${result.sentimentScore}%",

                            color = LightCream,

                            fontSize = 14.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        Text(
                            text =
                                "Based on resume text analysis",

                            color = SoftSage,

                            fontSize = 13.sp,

                            textAlign =
                                TextAlign.Center
                        )
                    }
                }
            }



            // KEYWORD ANALYSIS


            analysisResult?.let { result ->

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                OceanDark
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {

                        Text(
                            text =
                                "Keyword Analysis",

                            color = LightCream,

                            fontSize = 22.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        Text(
                            text =
                                selectedRole?.name ?: "",

                            color = SoftSage,

                            fontSize = 15.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(22.dp)
                        )


                        Text(
                            text =
                                "${result.percentage}%",

                            color = LightCream,

                            fontSize = 42.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Text(
                            text =
                                "Keyword Match",

                            color = OceanBlue,

                            fontSize = 14.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(22.dp)
                        )


                        Text(
                            text =
                                "Matched Keywords",

                            color = LightCream,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        if (
                            result.matchedKeywords.isNotEmpty()
                        ) {

                            result.matchedKeywords
                                .forEach { keyword ->

                                    Text(
                                        text =
                                            "✓ $keyword",

                                        color = SoftSage,

                                        fontSize = 14.sp,

                                        modifier =
                                            Modifier.padding(
                                                vertical = 3.dp
                                            )
                                    )
                                }

                        } else {

                            Text(
                                text =
                                    "No matching keywords found.",

                                color = SoftSage,

                                fontSize = 14.sp
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )


                        Text(
                            text =
                                "Missing Keywords",

                            color = LightCream,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        if (
                            result.missingKeywords.isNotEmpty()
                        ) {

                            result.missingKeywords
                                .forEach { keyword ->

                                    Text(
                                        text =
                                            "• $keyword",

                                        color = SoftSage,

                                        fontSize = 14.sp,

                                        modifier =
                                            Modifier.padding(
                                                vertical = 3.dp
                                            )
                                    )
                                }

                        } else {

                            Text(
                                text =
                                    "All required keywords found.",

                                color = SoftSage,

                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }



            // SENTIMENT ANALYSIS


            sentimentResult?.let { result ->

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {

                        Text(
                            text =
                                "Sentiment Analysis",

                            color = OceanDark,

                            fontSize = 22.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )


                        Text(
                            text =
                                "${result.sentimentScore}%",

                            color = OceanTeal,

                            fontSize = 40.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Text(
                            text =
                                result.sentimentLabel,

                            color = OceanBlue,

                            fontSize = 16.sp,

                            fontWeight =
                                FontWeight.SemiBold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(22.dp)
                        )


                        Text(
                            text =
                                "Positive Terms",

                            color = OceanDark,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        if (
                            result.positiveWords.isNotEmpty()
                        ) {

                            result.positiveWords
                                .forEach { word ->

                                    Text(
                                        text =
                                            "✓ $word",

                                        color = OceanTeal,

                                        fontSize = 14.sp,

                                        modifier =
                                            Modifier.padding(
                                                vertical = 3.dp
                                            )
                                    )
                                }

                        } else {

                            Text(
                                text =
                                    "No positive terms detected.",

                                color = OceanBlue,

                                fontSize = 14.sp
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )


                        Text(
                            text =
                                "Negative Terms",

                            color = OceanDark,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        if (
                            result.negativeWords.isNotEmpty()
                        ) {

                            result.negativeWords
                                .forEach { word ->

                                    Text(
                                        text =
                                            "• $word",

                                        color = OceanBlue,

                                        fontSize = 14.sp,

                                        modifier =
                                            Modifier.padding(
                                                vertical = 3.dp
                                            )
                                    )
                                }

                        } else {

                            Text(
                                text =
                                    "No negative terms detected.",

                                color = OceanBlue,

                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }



            // EXTRACTED RESUME TEXT


            if (resumeText.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text =
                                "Extracted Resume Text",

                            color = OceanDark,

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )


                        Text(
                            text =
                                resumeText,

                            color = OceanDark,

                            fontSize = 14.sp
                        )
                    }
                }
            }



            // FOOTER


            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = OceanBlue,

                            shape =
                                RoundedCornerShape(50)
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )


                Text(
                    text =
                        "Resume analysis made simple",

                    color = OceanBlue,

                    fontSize = 13.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
        }
    }
}



// GET FILE NAME


private fun getFileName(
    context: Context,
    uri: Uri
): String {

    var fileName =
        "Selected Resume"

    try {

        val cursor =
            context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

        cursor?.use {

            val nameIndex =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (
                nameIndex >= 0 &&
                it.moveToFirst()
            ) {

                fileName =
                    it.getString(nameIndex)
            }
        }

    } catch (e: Exception) {


    }

    return fileName
}