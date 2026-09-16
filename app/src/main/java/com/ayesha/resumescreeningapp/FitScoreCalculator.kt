package com.ayesha.resumescreeningapp

data class FitScoreResult(
    val keywordScore: Int,
    val sentimentScore: Int,
    val finalScore: Int
)

object FitScoreCalculator {

    private const val KEYWORD_WEIGHT = 0.80
    private const val SENTIMENT_WEIGHT = 0.20

    fun calculate(
        keywordScore: Int,
        sentimentScore: Int
    ): FitScoreResult {

        val finalScore =
            (
                    keywordScore * KEYWORD_WEIGHT +
                            sentimentScore * SENTIMENT_WEIGHT
                    ).toInt()

        return FitScoreResult(
            keywordScore = keywordScore,
            sentimentScore = sentimentScore,
            finalScore = finalScore
        )
    }
}