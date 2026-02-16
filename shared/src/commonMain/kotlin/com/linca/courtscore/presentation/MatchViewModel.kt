package com.linca.courtscore.presentation

import com.linca.courtscore.domain.model.GameScore
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
    private var gameWinner: Int? = null // 1 for player one, 2 for player two, null for no recent win
    private var setWinner: Int? = null // 1 for player one, 2 for player two, null for no recent set win

    private val _uiState = MutableStateFlow(
        MatchUiState.from(
            engine.getScore(),
            playerOneServing,
            showFinishDialog,
            showBackDialog,
            gameWinner,
            setWinner
        )
    )
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    fun onPlayerOneScored() {
        val previousGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val previousSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets

        engine.pointForPlayerOne()

        val newGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val newSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets

        gameWinner = if (newGames > previousGames) 1 else null
        setWinner = if (newSets > previousSets) 1 else null

        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun onPlayerTwoScored() {
        val previousGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val previousSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets

        engine.pointForPlayerTwo()

        val newGames =
            engine.getScore().currentSet.playerOneGames + engine.getScore().currentSet.playerTwoGames
        val newSets = engine.getScore().playerOneSets + engine.getScore().playerTwoSets

        gameWinner = if (newGames > previousGames) 2 else null
        setWinner = if (newSets > previousSets) 2 else null

        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun onAnimationComplete() {
        gameWinner = null
        updateUiState()
    }

    fun onSetAnimationComplete() {
        setWinner = null
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
        _uiState.value = MatchUiState.from(
            engine.getScore(),
            playerOneServing,
            showFinishDialog,
            showBackDialog,
            gameWinner,
            setWinner
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
    val setWinner: Int? = null
) {
    companion object {
        fun from(
            matchScore: MatchScore,
            playerOneServing: Boolean,
            showFinishDialog: Boolean,
            showBackDialog: Boolean,
            gameWinner: Int? = null,
            setWinner: Int? = null
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
                setWinner = setWinner
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