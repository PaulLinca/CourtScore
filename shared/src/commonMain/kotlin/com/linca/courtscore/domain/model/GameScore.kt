package com.linca.courtscore.domain.model

data class GameScore(
    val playerOne: Point = Point.LOVE,
    val playerTwo: Point = Point.LOVE,
    val isTieBreak: Boolean = false,
    val tieBreakPlayerOnePoints: Int = 0,
    val tieBreakPlayerTwoPoints: Int = 0
)