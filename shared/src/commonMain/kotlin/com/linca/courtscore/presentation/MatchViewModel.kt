package com.linca.courtscore.presentation

import com.linca.courtscore.domain.model.MatchScore
import com.linca.courtscore.domain.model.Point
import com.linca.courtscore.engine.MatchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel {

    private val engine = MatchEngine()

    private var playerOneServing = true
    private var showFinishDialog = false
    private var showBackDialog = false

    private val _uiState = MutableStateFlow(MatchUiState.from(engine.getScore(), playerOneServing, showFinishDialog, showBackDialog))
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    fun onPlayerOneScored() {
        engine.pointForPlayerOne()
        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun onPlayerTwoScored() {
        engine.pointForPlayerTwo()
        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun onUndo() {
        engine.undo()
        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun toggleServing() {
        playerOneServing = !playerOneServing
        updateUiState()
    }

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
        _uiState.value = MatchUiState.from(engine.getScore(), playerOneServing, showFinishDialog, showBackDialog)
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
    val showBackDialog: Boolean
) {
    companion object {
        fun from(matchScore: MatchScore, playerOneServing: Boolean, showFinishDialog: Boolean, showBackDialog: Boolean): MatchUiState {
            return MatchUiState(
                playerOneGameScore = formatGameScore(matchScore.currentGame, isPlayerOne = true),
                playerTwoGameScore = formatGameScore(matchScore.currentGame, isPlayerTwo = true),
                playerOneSetScores = buildPlayerOneSetScoresList(matchScore),
                playerTwoSetScores = buildPlayerTwoSetScoresList(matchScore),
                isFinished = matchScore.isFinished,
                playerOneServing = playerOneServing,
                playerOneWon = matchScore.playerOneSets >= 2,
                playerTwoWon = matchScore.playerTwoSets >= 2,
                showFinishDialog = showFinishDialog,
                showBackDialog = showBackDialog
            )
        }

        private fun formatGameScore(gameScore: com.linca.courtscore.domain.model.GameScore, isPlayerOne: Boolean = false, isPlayerTwo: Boolean = false): String {
            return if (gameScore.isTieBreak) {
                // In tiebreak, display actual numerical scores
                if (isPlayerOne) {
                    gameScore.tieBreakPlayerOnePoints.toString()
                } else {
                    gameScore.tieBreakPlayerTwoPoints.toString()
                }
            } else {
                // Standard game scoring
                val point = if (isPlayerOne) gameScore.playerOne else gameScore.playerTwo
                when (point) {
                    Point.LOVE -> "0"
                    Point.FIFTEEN -> "15"
                    Point.THIRTY -> "30"
                    Point.FORTY -> "40"
                    Point.ADVANTAGE -> "AD"
                }
            }
        }

        private fun buildPlayerOneSetScoresList(matchScore: MatchScore): List<Int> {
            // Create a list with up to 3 set scores
            val scores = mutableListOf<Int>()

            // Add scores from completed sets
            matchScore.completedSets.forEach { setScore ->
                scores.add(setScore.playerOneGames)
            }

            // Add current set games
            scores.add(matchScore.currentSet.playerOneGames)

            // Fill up to 3 sets total with zeros
            while (scores.size < 3) {
                scores.add(0)
            }

            return scores.take(3)
        }

        private fun buildPlayerTwoSetScoresList(matchScore: MatchScore): List<Int> {
            val scores = mutableListOf<Int>()

            // Add scores from completed sets
            matchScore.completedSets.forEach { setScore ->
                scores.add(setScore.playerTwoGames)
            }

            // Add current set games
            scores.add(matchScore.currentSet.playerTwoGames)

            // Fill up to 3 sets total with zeros
            while (scores.size < 3) {
                scores.add(0)
            }

            return scores.take(3)
        }
    }
}