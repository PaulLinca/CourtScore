package com.linca.courtscore.presentation

import com.linca.courtscore.domain.model.ScoringType
import com.linca.courtscore.engine.BadmintonStrategy
import com.linca.courtscore.engine.FootballStrategy
import com.linca.courtscore.engine.PadelStrategy
import com.linca.courtscore.engine.SportStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel(
    private val strategy: SportStrategy = PadelStrategy()
) {
    private var playerOneServing = true
    private var serveFromRight = true
    private val serveFromRightHistory = mutableListOf<Boolean>()
    private var showFinishDialog = false
    private var showBackDialog = false
    private var gameWinner: Int? = null
    private var setWinner: Int? = null
    private var goldenPointWinner: Int? = null

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    fun onPlayerOneScored() {
        val result = strategy.pointForPlayerOne()
        serveFromRightHistory.add(serveFromRight)
        if (result.scoringUnitWinner != null) {
            playerOneServing = !playerOneServing
            serveFromRight = true
        } else {
            serveFromRight = !serveFromRight
        }
        gameWinner = result.scoringUnitWinner
        setWinner = result.periodWinner
        goldenPointWinner = result.goldenPointWinner
        updateUiState()
    }

    fun onPlayerTwoScored() {
        val result = strategy.pointForPlayerTwo()
        serveFromRightHistory.add(serveFromRight)
        if (result.scoringUnitWinner != null) {
            playerOneServing = !playerOneServing
            serveFromRight = true
        } else {
            serveFromRight = !serveFromRight
        }
        gameWinner = result.scoringUnitWinner
        setWinner = result.periodWinner
        goldenPointWinner = result.goldenPointWinner
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

    fun onGoldenPointAnimationComplete() {
        goldenPointWinner = null
        updateUiState()
    }

    fun onUndo() {
        val result = strategy.undo()
        if (result.scoringUnitWinner != null) playerOneServing = !playerOneServing
        if (serveFromRightHistory.isNotEmpty()) {
            serveFromRight = serveFromRightHistory.removeAt(serveFromRightHistory.lastIndex)
        }
        goldenPointWinner = null
        updateUiState()
    }

    fun toggleServing() {
        playerOneServing = !playerOneServing
        updateUiState()
    }

    fun setScoringType(type: ScoringType) {
        strategy.setScoringType(type)
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
        strategy.finishMatch()
        showFinishDialog = false
        updateUiState()
    }

    fun onFinishCancelled() {
        showFinishDialog = false
        updateUiState()
    }

    fun onBackPressed(): Boolean {
        return if (strategy.isFinished()) {
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
        _uiState.value = buildUiState()
    }

    private fun buildUiState(): MatchUiState {
        val data = strategy.toDisplayData()
        return MatchUiState(
            playerOneGameScore = data.playerOneMainScore,
            playerTwoGameScore = data.playerTwoMainScore,
            playerOneSetScores = data.playerOneSubScores,
            playerTwoSetScores = data.playerTwoSubScores,
            isFinished = data.isFinished,
            playerOneServing = data.playerOneServing ?: playerOneServing,
            playerOneWon = data.playerOneWins,
            playerTwoWon = data.playerTwoWins,
            showFinishDialog = showFinishDialog,
            showBackDialog = showBackDialog,
            gameWinner = gameWinner,
            setWinner = setWinner,
            scoringType = data.scoringType,
            showScoringTypeDialog = data.showConfigDialog,
            goldenPointWinner = goldenPointWinner,
            showSubScores = data.showSubScores,
            showServingIndicator = data.showServingIndicator,
            serveFromRight = data.serveFromRight ?: serveFromRight
        )
    }

    companion object {
        // Factory helpers for Swift interop — avoids exposing SportStrategy at the ObjC boundary.
        fun forPadel(): MatchViewModel = MatchViewModel(PadelStrategy())
        fun forFootball(): MatchViewModel = MatchViewModel(FootballStrategy())
        fun forBadminton(): MatchViewModel = MatchViewModel(BadmintonStrategy())
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
    val goldenPointWinner: Int? = null,
    val showSubScores: Boolean = true,
    val showServingIndicator: Boolean = true,
    val serveFromRight: Boolean = true
)
