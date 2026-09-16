package com.ayesha.resumescreeningapp

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object PdfParser {

    fun extractText(
        context: Context,
        uri: Uri
    ): String {

        return try {

            // Initialize PDFBox resources.
            PDFBoxResourceLoader.init(context.applicationContext)

            val inputStream =
                context.contentResolver.openInputStream(uri)
                    ?: return ""

            inputStream.use { stream ->

                val document =
                    PDDocument.load(stream)

                document.use { pdfDocument ->

                    val pdfTextStripper =
                        PDFTextStripper()

                    pdfTextStripper
                        .getText(pdfDocument)
                        .trim()
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

            ""
        }
    }
}