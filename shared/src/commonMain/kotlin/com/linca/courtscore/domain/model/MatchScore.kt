package com.linca.courtscore.domain.model

data class MatchScore(
    val currentGame: GameScore = GameScore(),
    val currentSet: SetScore = SetScore(),
    val playerOneSets: Int = 0,
    val playerTwoSets: Int = 0,
    val completedSets: List<SetScore> = emptyList(),
    val isFinished: Boolean = false
)