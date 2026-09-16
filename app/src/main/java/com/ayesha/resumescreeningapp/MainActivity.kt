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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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

    // Android context
    val context = LocalContext.current

    // Selected file name
    var selectedFileName by remember {
        mutableStateOf<String?>(null)
    }

    // Selected file URI
    var selectedFileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // Extracted resume text
    var resumeText by remember {
        mutableStateOf("")
    }

    // Status message
    var message by remember {
        mutableStateOf("")
    }


    // FILE PICKER


    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            // Save selected URI
            selectedFileUri = uri

            // Get file name
            selectedFileName = getFileName(
                context = context,
                uri = uri
            )


            // TXT FILE


            if (
                selectedFileName
                    ?.lowercase()
                    ?.endsWith(".txt") == true
            ) {

                try {

                    val inputStream =
                        context.contentResolver.openInputStream(uri)

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

            }


            // PDF FILE


            else {

                resumeText = ""

                message =
                    "PDF selected."
            }
        }


    // SCREEN


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightCream
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightCream)
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // APP LOGO


            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(
                        color = OceanDark,
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "RS",
                    color = LightCream,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
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
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "AI-powered resume analysis",
                color = OceanBlue,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )


            // UPLOAD CARD


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Upload your resume",
                        color = OceanDark,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Select a PDF or text resume to begin analysis.",
                        color = OceanBlue,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )


                    // UPLOAD BUTTON


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
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OceanTeal,
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            text = "Upload Resume",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "Supported formats: PDF • TXT",
                        color = OceanBlue,
                        fontSize = 13.sp
                    )
                }
            }


            // SELECTED FILE CARD


            if (selectedFileName != null) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftSage
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Selected Resume",
                            color = OceanDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        Text(
                            text = selectedFileName ?: "",
                            color = OceanDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                if (resumeText.isNotEmpty()) {
                                    "Text loaded successfully"
                                } else {
                                    "Ready for analysis"
                                },
                            color = OceanTeal,
                            fontSize = 13.sp
                        )
                    }
                }
            }


            // STATUS MESSAGE


            if (message.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = message,
                    color = OceanTeal,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }


            // TEXT PREVIEW


            if (resumeText.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Resume Text",
                            color = OceanDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = resumeText,
                            color = OceanDark,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )


            // BOTTOM INFORMATION


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = OceanBlue,
                            shape = RoundedCornerShape(50)
                        )
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Resume analysis made simple",
                    color = OceanBlue,
                    fontSize = 13.sp
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}


// GET FILE NAME


private fun getFileName(
    context: Context,
    uri: Uri
): String {

    var fileName = "Selected Resume"

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