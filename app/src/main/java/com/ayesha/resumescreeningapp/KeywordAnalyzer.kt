package com.ayesha.resumescreeningapp

data class KeywordAnalysisResult(
    val matchedKeywords: List<String>,
    val missingKeywords: List<String>,
    val percentage: Int
)

object KeywordAnalyzer {

    fun analyze(
        resumeText: String,
        jobRole: JobRole
    ): KeywordAnalysisResult {

        val normalizedResume =
            normalizeText(resumeText)

        val matchedKeywords =
            mutableListOf<String>()

        val missingKeywords =
            mutableListOf<String>()

        for (keyword in jobRole.keywords) {

            val normalizedKeyword =
                normalizeText(keyword)

            if (
                normalizedResume.contains(
                    normalizedKeyword
                )
            ) {

                matchedKeywords.add(keyword)

            } else {

                missingKeywords.add(keyword)
            }
        }

        val totalKeywords =
            jobRole.keywords.size

        val percentage =
            if (totalKeywords > 0) {

                (
                        matchedKeywords.size * 100
                        ) / totalKeywords

            } else {

                0
            }

        return KeywordAnalysisResult(
            matchedKeywords = matchedKeywords,
            missingKeywords = missingKeywords,
            percentage = percentage
        )
    }

    private fun normalizeText(
        text: String
    ): String {

        return text
            .lowercase()
            .replace(
                Regex("[^a-z0-9+#.]"),
                " "
            )
            .replace(
                Regex("\\s+"),
                " "
            )
            .trim()
    }
}