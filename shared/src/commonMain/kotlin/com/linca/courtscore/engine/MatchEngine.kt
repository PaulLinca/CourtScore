package com.linca.courtscore.engine

import com.linca.courtscore.domain.model.GameScore
import com.linca.courtscore.domain.model.MatchScore
import com.linca.courtscore.domain.model.Point
import com.linca.courtscore.domain.model.SetScore

class MatchEngine(
    private val setsToWin: Int = 2,
    private val gamesToWinSet: Int = 6,
    private val tieBreakAt: Int = 6,
    private val tieBreakPointsToWin: Int = 7
) {
    private val history = mutableListOf<MatchScore>()

    private var score = MatchScore()

    private var tieBreakPlayerOnePoints: Int = 0
    private var tieBreakPlayerTwoPoints: Int = 0

    fun getScore(): MatchScore = score

    fun pointForPlayerOne() = addPoint(winnerIsPlayerOne = true)

    fun pointForPlayerTwo() = addPoint(winnerIsPlayerOne = false)

    fun finishMatch() {
        score = score.copy(isFinished = true)
    }

    fun undo() {
        if (history.isNotEmpty()) {
            score = history.removeAt(history.lastIndex)
            // conservative: if we undo into/out of a tiebreak, recompute counters from displayed points.
            if (isTieBreakGame(score.currentSet)) {
                tieBreakPlayerOnePoints = pointToTieBreakCount(score.currentGame.playerOne)
                tieBreakPlayerTwoPoints = pointToTieBreakCount(score.currentGame.playerTwo)
            } else {
                tieBreakPlayerOnePoints = 0
                tieBreakPlayerTwoPoints = 0
            }
        }
    }

    private fun addPoint(winnerIsPlayerOne: Boolean) {

        history.add(score)

        score = if (isTieBreakGame(score.currentSet)) {
            applyTieBreakPoint(score, winnerIsPlayerOne)
        } else {
            applyStandardPoint(score, winnerIsPlayerOne)
        }
    }

    private fun isTieBreakGame(set: SetScore): Boolean {
        return set.playerOneGames == tieBreakAt && set.playerTwoGames == tieBreakAt
    }

    private fun applyTieBreakPoint(current: MatchScore, winnerIsPlayerOne: Boolean): MatchScore {
        if (winnerIsPlayerOne) tieBreakPlayerOnePoints++ else tieBreakPlayerTwoPoints++

        val hasWinner =
            (tieBreakPlayerOnePoints >= tieBreakPointsToWin || tieBreakPlayerTwoPoints >= tieBreakPointsToWin) &&
                    kotlin.math.abs(tieBreakPlayerOnePoints - tieBreakPlayerTwoPoints) >= 2

        return if (hasWinner) {
            val playerOneWon = tieBreakPlayerOnePoints > tieBreakPlayerTwoPoints

            tieBreakPlayerOnePoints = 0
            tieBreakPlayerTwoPoints = 0

            val playerOneGames = current.currentSet.playerOneGames + if (playerOneWon) 1 else 0
            val playerTwoGames = current.currentSet.playerTwoGames + if (!playerOneWon) 1 else 0

            finishGameAndMaybeSet(
                current.copy(
                    currentGame = GameScore(),
                    currentSet = current.currentSet.copy(
                        playerOneGames = playerOneGames,
                        playerTwoGames = playerTwoGames
                    )
                )
            )
        } else {
            current.copy(
                currentGame = GameScore(
                    playerOne = tieBreakCountToPoint(tieBreakPlayerOnePoints),
                    playerTwo = tieBreakCountToPoint(tieBreakPlayerTwoPoints),
                    isTieBreak = true,
                    tieBreakPlayerOnePoints = tieBreakPlayerOnePoints,
                    tieBreakPlayerTwoPoints = tieBreakPlayerTwoPoints
                )
            )
        }
    }

    private fun applyStandardPoint(current: MatchScore, winnerIsPlayerOne: Boolean): MatchScore {
        val currentGame = current.currentGame

        if (currentGame.playerOne == Point.ADVANTAGE || currentGame.playerTwo == Point.ADVANTAGE) {
            return when {
                winnerIsPlayerOne && currentGame.playerOne == Point.ADVANTAGE -> winGame(
                    current,
                    winnerIsPlayerOne = true
                )

                !winnerIsPlayerOne && currentGame.playerTwo == Point.ADVANTAGE -> winGame(
                    current,
                    winnerIsPlayerOne = false
                )

                else -> current.copy(
                    currentGame = GameScore(
                        playerOne = Point.FORTY,
                        playerTwo = Point.FORTY
                    )
                )
            }
        }

        if (currentGame.playerOne == Point.FORTY && currentGame.playerTwo == Point.FORTY) {
            return current.copy(
                currentGame = if (winnerIsPlayerOne) {
                    GameScore(playerOne = Point.ADVANTAGE, playerTwo = Point.FORTY)
                } else {
                    GameScore(playerOne = Point.FORTY, playerTwo = Point.ADVANTAGE)
                }
            )
        }

        if (winnerIsPlayerOne && currentGame.playerOne == Point.FORTY && currentGame.playerTwo != Point.FORTY) {
            return winGame(current, winnerIsPlayerOne = true)
        }
        if (!winnerIsPlayerOne && currentGame.playerTwo == Point.FORTY && currentGame.playerOne != Point.FORTY) {
            return winGame(current, winnerIsPlayerOne = false)
        }

        val newGame = if (winnerIsPlayerOne) {
            currentGame.copy(playerOne = nextStandardPoint(currentGame.playerOne))
        } else {
            currentGame.copy(playerTwo = nextStandardPoint(currentGame.playerTwo))
        }

        return current.copy(currentGame = newGame)
    }

    private fun nextStandardPoint(p: Point): Point = when (p) {
        Point.LOVE -> Point.FIFTEEN
        Point.FIFTEEN -> Point.THIRTY
        Point.THIRTY -> Point.FORTY
        Point.FORTY, Point.ADVANTAGE -> p
    }

    private fun winGame(current: MatchScore, winnerIsPlayerOne: Boolean): MatchScore {
        val newSet = if (winnerIsPlayerOne) {
            current.currentSet.copy(playerOneGames = current.currentSet.playerOneGames + 1)
        } else {
            current.currentSet.copy(playerTwoGames = current.currentSet.playerTwoGames + 1)
        }

        return finishGameAndMaybeSet(
            current.copy(
                currentGame = GameScore(),
                currentSet = newSet
            )
        )
    }

    private fun finishGameAndMaybeSet(current: MatchScore): MatchScore {
        val set = current.currentSet

        val playerOneWonSet = hasWonSet(set.playerOneGames, set.playerTwoGames)
        val playerTwoWonSet = hasWonSet(set.playerTwoGames, set.playerOneGames)

        if (!playerOneWonSet && !playerTwoWonSet) return current

        tieBreakPlayerOnePoints = 0
        tieBreakPlayerTwoPoints = 0

        val newPlayerOneSets = current.playerOneSets + if (playerOneWonSet) 1 else 0
        val newPlayerTwoSets = current.playerTwoSets + if (playerTwoWonSet) 1 else 0

        return current.copy(
            playerOneSets = newPlayerOneSets,
            playerTwoSets = newPlayerTwoSets,
            completedSets = current.completedSets + set,
            currentSet = SetScore(),
            currentGame = GameScore(),
            isFinished = false
        )
    }

    private fun hasWonSet(winnerGames: Int, loserGames: Int): Boolean {
        // Standard: first to 6 by 2; at 6-6 we go to tiebreak and the set ends 7-6.
        if (winnerGames >= gamesToWinSet && (winnerGames - loserGames) >= 2) return true
        if (winnerGames == tieBreakAt + 1 && loserGames == tieBreakAt) return true
        return false
    }

    // Used only to rebuild tie break counters after an undo; it’s lossy for counts > 4.
    private fun pointToTieBreakCount(p: Point): Int = when (p) {
        Point.LOVE -> 0
        Point.FIFTEEN -> 1
        Point.THIRTY -> 2
        Point.FORTY -> 3
        Point.ADVANTAGE -> 4
    }

    private fun tieBreakCountToPoint(count: Int): Point = when (count) {
        0 -> Point.LOVE
        1 -> Point.FIFTEEN
        2 -> Point.THIRTY
        3 -> Point.FORTY
        else -> Point.ADVANTAGE
    }
}
