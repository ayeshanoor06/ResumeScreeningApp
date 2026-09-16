package com.ayesha.resumescreeningapp

data class SentimentAnalysisResult(
    val positiveWords: List<String>,
    val negativeWords: List<String>,
    val positiveScore: Int,
    val negativeScore: Int,
    val sentimentScore: Int,
    val sentimentLabel: String
)

object SentimentAnalyzer {

    private val positiveWords = setOf(
        "achieved",
        "achievement",
        "adaptable",
        "advanced",
        "completed",
        "creative",
        "developed",
        "efficient",
        "experienced",
        "improved",
        "innovative",
        "leadership",
        "motivated",
        "organized",
        "professional",
        "reliable",
        "successful",
        "success",
        "teamwork",
        "technical",
        "skilled",
        "strong",
        "excellent",
        "responsible",
        "dedicated",
        "problem-solving",
        "collaborative",
        "effective",
        "productive",
        "confident"
    )

    private val negativeWords = setOf(
        "failed",
        "failure",
        "unable",
        "weak",
        "poor",
        "problem",
        "problems",
        "difficult",
        "difficulty",
        "error",
        "errors",
        "late",
        "conflict",
        "conflicts",
        "limited",
        "lack",
        "lacking",
        "unsuccessful",
        "inexperienced",
        "rejected",
        "negative",
        "issue",
        "issues"
    )

    fun analyze(
        resumeText: String
    ): SentimentAnalysisResult {

        val words =
            normalizeText(resumeText)
                .split(" ")
                .filter {
                    it.isNotBlank()
                }

        val foundPositiveWords =
            mutableListOf<String>()

        val foundNegativeWords =
            mutableListOf<String>()

        for (word in words) {

            if (
                positiveWords.contains(word) &&
                !foundPositiveWords.contains(word)
            ) {

                foundPositiveWords.add(word)
            }

            if (
                negativeWords.contains(word) &&
                !foundNegativeWords.contains(word)
            ) {

                foundNegativeWords.add(word)
            }
        }

        val positiveScore =
            foundPositiveWords.size

        val negativeScore =
            foundNegativeWords.size

        val totalScore =
            positiveScore + negativeScore

        val sentimentScore =
            if (totalScore == 0) {

                50

            } else {

                (
                        positiveScore * 100
                        ) / totalScore
            }

        val sentimentLabel =
            when {

                sentimentScore >= 70 ->
                    "Positive"

                sentimentScore >= 40 ->
                    "Neutral"

                else ->
                    "Negative"
            }

        return SentimentAnalysisResult(
            positiveWords =
                foundPositiveWords,
            negativeWords =
                foundNegativeWords,
            positiveScore =
                positiveScore,
            negativeScore =
                negativeScore,
            sentimentScore =
                sentimentScore,
            sentimentLabel =
                sentimentLabel
        )
    }

    private fun normalizeText(
        text: String
    ): String {

        return text
            .lowercase()
            .replace(
                Regex("[^a-z0-9+#-]"),
                " "
            )
            .replace(
                Regex("\\s+"),
                " "
            )
            .trim()
    }
}