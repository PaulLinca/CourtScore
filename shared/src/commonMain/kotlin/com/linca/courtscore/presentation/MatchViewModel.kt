package com.linca.courtscore.presentation

import com.linca.courtscore.domain.model.GameScore
import com.linca.courtscore.domain.model.MatchScore
import com.linca.courtscore.domain.model.Point
import com.linca.courtscore.domain.model.ScoringType
import com.linca.courtscore.domain.model.pointToDisplayString
import com.linca.courtscore.engine.MatchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel {

    private val engine = MatchEngine()

    private var playerOneServing = true
    private var showFinishDialog = false
    private var showBackDialog = false
    private var gameWinner: Int? = null // 1 for player one, 2 for player two, null for no recent win
    private var setWinner: Int? = null // 1 for player one, 2 for player two, null for no recent set win
    private var scoringTypeChosen = false
    private var showScoringTypeDialog = false
    private var goldenPointWinner: Int? = null

    private val _uiState = MutableStateFlow(
        MatchUiState.from(
            engine.getScore(),
            playerOneServing,
            showFinishDialog,
            showBackDialog,
            gameWinner,
            setWinner,
            engine.getScoringType(),
            showScoringTypeDialog,
            goldenPointWinner
        )
    )
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    fun onPlayerOneScored() {
        val previousGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val previousSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets
        val wasAtGoldenPointDeuce = isAtGoldenPointDeuce()

        engine.pointForPlayerOne()

        val newGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val newSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets

        gameWinner = if (newGames > previousGames) 1 else null
        setWinner = if (newSets > previousSets) 1 else null
        goldenPointWinner = if (wasAtGoldenPointDeuce && newGames > previousGames) 1 else null

        playerOneServing = !playerOneServing
        checkForFirstDeuce()
        updateUiState()
    }

    fun onPlayerTwoScored() {
        val previousGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val previousSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets
        val wasAtGoldenPointDeuce = isAtGoldenPointDeuce()

        engine.pointForPlayerTwo()

        val newGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val newSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets

        gameWinner = if (newGames > previousGames) 2 else null
        setWinner = if (newSets > previousSets) 2 else null
        goldenPointWinner = if (wasAtGoldenPointDeuce && newGames > previousGames) 2 else null

        playerOneServing = !playerOneServing
        checkForFirstDeuce()
        updateUiState()
    }

    private fun isAtGoldenPointDeuce(): Boolean {
        val game = engine.getScore().currentGame
        return engine.getScoringType() == ScoringType.GOLDEN_POINT
            && !game.isTieBreak
            && game.playerOne == Point.FORTY
            && game.playerTwo == Point.FORTY
    }

    private fun checkForFirstDeuce() {
        if (!scoringTypeChosen) {
            val game = engine.getScore().currentGame
            if (!game.isTieBreak && game.playerOne == Point.FORTY && game.playerTwo == Point.FORTY) {
                showScoringTypeDialog = true
            }
        }
    }


    fun onAnimationComplete() {
        gameWinner = null
        updateUiState()
    }

    fun onSetAnimationComplete() {
        setWinner = null
        updateUiState()
    }

    fun onGoldenPointAnimationComplete() {
        goldenPointWinner = null
        updateUiState()
    }

    fun onUndo() {
        engine.undo()
        playerOneServing = !playerOneServing
        showScoringTypeDialog = false
        goldenPointWinner = null
        updateUiState()
    }

    fun toggleServing() {
        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun setScoringType(type: ScoringType) {
        engine.setScoringType(type)
        scoringTypeChosen = true
        showScoringTypeDialog = false
        updateUiState()
    }

    // Convenience helpers for Swift interop (avoids exposing ScoringType in the ObjC interface)
    fun setScoringTypeAdvantage() = setScoringType(ScoringType.ADVANTAGE)
    fun setScoringTypeGoldenPoint() = setScoringType(ScoringType.GOLDEN_POINT)
    fun setScoringTypeStarPoint() = setScoringType(ScoringType.STAR_POINT)

    fun onFinishClicked() {
        showFinishDialog = true
        updateUiState()
    }

    fun onFinishConfirmed() {
        engine.finishMatch()
        showFinishDialog = false
        updateUiState()
    }

    fun onFinishCancelled() {
        showFinishDialog = false
        updateUiState()
    }

    fun onBackPressed(): Boolean {
        return if (engine.getScore().isFinished) {
            false
        } else {
            showBackDialog = true
            updateUiState()
            true
        }
    }

    fun onBackConfirmed() {
        showBackDialog = false
        updateUiState()
    }

    fun onBackCancelled() {
        showBackDialog = false
        updateUiState()
    }

    private fun updateUiState() {
        _uiState.value = MatchUiState.from(
            engine.getScore(),
            playerOneServing,
            showFinishDialog,
            showBackDialog,
            gameWinner,
            setWinner,
            engine.getScoringType(),
            showScoringTypeDialog,
            goldenPointWinner
        )
    }
}

data class MatchUiState(
    val playerOneGameScore: String,
    val playerTwoGameScore: String,
    val playerOneSetScores: List<Int>,
    val playerTwoSetScores: List<Int>,
    val isFinished: Boolean,
    val playerOneServing: Boolean,
    val playerOneWon: Boolean,
    val playerTwoWon: Boolean,
    val showFinishDialog: Boolean,
    val showBackDialog: Boolean,
    val gameWinner: Int? = null,
    val setWinner: Int? = null,
    val scoringType: ScoringType = ScoringType.ADVANTAGE,
    val showScoringTypeDialog: Boolean = false,
    val goldenPointWinner: Int? = null
) {
    companion object {
        fun from(
            matchScore: MatchScore,
            playerOneServing: Boolean,
            showFinishDialog: Boolean,
            showBackDialog: Boolean,
            gameWinner: Int? = null,
            setWinner: Int? = null,
            scoringType: ScoringType = ScoringType.ADVANTAGE,
            showScoringTypeDialog: Boolean = false,
            goldenPointWinner: Int? = null
        ): MatchUiState {
            return MatchUiState(
                playerOneGameScore = formatGameScore(matchScore.currentGame, isPlayerOne = true),
                playerTwoGameScore = formatGameScore(matchScore.currentGame),
                playerOneSetScores = buildPlayerOneSetScoresList(matchScore),
                playerTwoSetScores = buildPlayerTwoSetScoresList(matchScore),
                isFinished = matchScore.isFinished,
                playerOneServing = playerOneServing,
                playerOneWon = matchScore.playerOneSets >= 2,
                playerTwoWon = matchScore.playerTwoSets >= 2,
                showFinishDialog = showFinishDialog,
                showBackDialog = showBackDialog,
                gameWinner = gameWinner,
                setWinner = setWinner,
                scoringType = scoringType,
                showScoringTypeDialog = showScoringTypeDialog,
                goldenPointWinner = goldenPointWinner
            )
        }

        private fun formatGameScore(
            gameScore: GameScore,
            isPlayerOne: Boolean = false
        ): String {
            return if (gameScore.isTieBreak) {
                // In tiebreak, display actual numerical scores
                if (isPlayerOne) {
                    gameScore.tieBreakPlayerOnePoints.toString()
                } else {
                    gameScore.tieBreakPlayerTwoPoints.toString()
                }
            } else {
                val point = if (isPlayerOne) gameScore.playerOne else gameScore.playerTwo
                pointToDisplayString(point)
            }
        }

        private fun buildPlayerOneSetScoresList(matchScore: MatchScore): List<Int> {
            val scores = mutableListOf<Int>()

            matchScore.completedSets.forEach { setScore ->
                scores.add(setScore.playerOneGames)
            }

            scores.add(matchScore.currentSet.playerOneGames)

            while (scores.size < 3) {
                scores.add(0)
            }

            return scores.take(3)
        }

        private fun buildPlayerTwoSetScoresList(matchScore: MatchScore): List<Int> {
            val scores = mutableListOf<Int>()

            matchScore.completedSets.forEach { setScore ->
                scores.add(setScore.playerTwoGames)
            }

            scores.add(matchScore.currentSet.playerTwoGames)

            while (scores.size < 3) {
                scores.add(0)
            }

            return scores.take(3)
        }
    }
}