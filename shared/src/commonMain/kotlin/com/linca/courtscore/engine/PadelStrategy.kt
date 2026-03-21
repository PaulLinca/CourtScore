package com.linca.courtscore.engine

import com.linca.courtscore.domain.model.GameScore
import com.linca.courtscore.domain.model.MatchScore
import com.linca.courtscore.domain.model.Point
import com.linca.courtscore.domain.model.ScoringType
import com.linca.courtscore.domain.model.pointToDisplayString

/**
 * Padel-specific scoring strategy. Wraps the existing [MatchEngine] and isolates all
 * padel rules (deuce/advantage, golden point, tie-break, sets) behind [SportStrategy].
 */
class PadelStrategy : SportStrategy {

    private val engine = MatchEngine()
    private var scoringTypeChosen = false
    private var showConfigDialog = false

    override fun pointForPlayerOne(): ScoringResult = score(player = 1)

    override fun pointForPlayerTwo(): ScoringResult = score(player = 2)

    private fun score(player: Int): ScoringResult {
        val prevGames = totalGames()
        val prevSets = totalSets()
        val wasAtGoldenPointDeuce = isAtGoldenPointDeuce()

        if (player == 1) engine.pointForPlayerOne() else engine.pointForPlayerTwo()

        val gameWon = totalGames() > prevGames
        val setWon = totalSets() > prevSets

        checkForFirstDeuce()

        return ScoringResult(
            scoringUnitWinner = if (gameWon) player else null,
            periodWinner = if (setWon) player else null,
            goldenPointWinner = if (wasAtGoldenPointDeuce && gameWon) player else null
        )
    }

    override fun undo(): ScoringResult {
        val prevGames = totalGames()
        val prevSets = totalSets()
        engine.undo()
        showConfigDialog = false
        val gameReverted = totalGames() < prevGames || totalSets() < prevSets
        return ScoringResult(scoringUnitWinner = if (gameReverted) 0 else null)
    }

    override fun finishMatch() = engine.finishMatch()

    override fun isFinished() = engine.getScore().isFinished

    override fun toDisplayData(): SportDisplayData {
        val score = engine.getScore()
        return SportDisplayData(
            playerOneMainScore = formatGameScore(score.currentGame, isPlayerOne = true),
            playerTwoMainScore = formatGameScore(score.currentGame, isPlayerOne = false),
            playerOneSubScores = buildSubScores(score, isPlayerOne = true),
            playerTwoSubScores = buildSubScores(score, isPlayerOne = false),
            playerOneWins = score.playerOneSets >= 2,
            playerTwoWins = score.playerTwoSets >= 2,
            isFinished = score.isFinished,
            showConfigDialog = showConfigDialog,
            scoringType = engine.getScoringType()
        )
    }

    override fun setScoringType(type: ScoringType) {
        engine.setScoringType(type)
        scoringTypeChosen = true
        showConfigDialog = false
    }

    override fun getScoringType(): ScoringType = engine.getScoringType()

    private fun checkForFirstDeuce() {
        if (!scoringTypeChosen) {
            val game = engine.getScore().currentGame
            if (!game.isTieBreak && game.playerOne == Point.FORTY && game.playerTwo == Point.FORTY) {
                showConfigDialog = true
            }
        }
    }

    private fun isAtGoldenPointDeuce(): Boolean {
        val game = engine.getScore().currentGame
        return engine.getScoringType() == ScoringType.GOLDEN_POINT
            && !game.isTieBreak
            && game.playerOne == Point.FORTY
            && game.playerTwo == Point.FORTY
    }

    private fun totalGames() =
        engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames

    private fun totalSets() =
        engine.getScore().playerOneSets + engine.getScore().playerTwoSets

    private fun formatGameScore(game: GameScore, isPlayerOne: Boolean): String {
        return if (game.isTieBreak) {
            if (isPlayerOne) game.tieBreakPlayerOnePoints.toString()
            else game.tieBreakPlayerTwoPoints.toString()
        } else {
            pointToDisplayString(if (isPlayerOne) game.playerOne else game.playerTwo)
        }
    }

    private fun buildSubScores(score: MatchScore, isPlayerOne: Boolean): List<Int> {
        val scores = mutableListOf<Int>()
        score.completedSets.forEach { set ->
            scores.add(if (isPlayerOne) set.playerOneGames else set.playerTwoGames)
        }
        scores.add(if (isPlayerOne) score.currentSet.playerOneGames else score.currentSet.playerTwoGames)
        while (scores.size < 3) scores.add(0)
        return scores.take(3)
    }
}
