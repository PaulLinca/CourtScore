package com.linca.courtscore.engine

import com.linca.courtscore.domain.model.ScoringType

/**
 * Football (soccer) scoring strategy.
 *
 * Rules: two teams score goals; the team with more goals at the end of the match wins.
 * Draws are valid (no winner). Undo is supported for the full goal history.
 *
 * Period tracking is not yet implemented; sub-scores are zeroed placeholders.
 * Add [advancePeriod] and period-goal tracking as a follow-up when period display is required.
 */
class FootballStrategy : SportStrategy {

    private data class State(
        val homeGoals: Int = 0,
        val awayGoals: Int = 0,
        val isFinished: Boolean = false
    )

    private var state = State()
    private val history = mutableListOf<State>()

    override fun pointForPlayerOne(): ScoringResult {
        history.add(state)
        state = state.copy(homeGoals = state.homeGoals + 1)
        return ScoringResult(scoringUnitWinner = 1)
    }

    override fun pointForPlayerTwo(): ScoringResult {
        history.add(state)
        state = state.copy(awayGoals = state.awayGoals + 1)
        return ScoringResult(scoringUnitWinner = 2)
    }

    override fun undo(): ScoringResult {
        if (history.isNotEmpty()) {
            state = history.removeAt(history.lastIndex)
        }
        return ScoringResult()
    }

    override fun finishMatch() {
        state = state.copy(isFinished = true)
    }

    override fun isFinished() = state.isFinished

    override fun toDisplayData() = SportDisplayData(
        playerOneMainScore = state.homeGoals.toString(),
        playerTwoMainScore = state.awayGoals.toString(),
        playerOneSubScores = listOf(0, 0, 0),
        playerTwoSubScores = listOf(0, 0, 0),
        playerOneWins = state.isFinished && state.homeGoals > state.awayGoals,
        playerTwoWins = state.isFinished && state.awayGoals > state.homeGoals,
        isFinished = state.isFinished,
        showSubScores = false,
        showServingIndicator = false
    )

    // Football has no scoring-type configuration.
    override fun setScoringType(type: ScoringType) = Unit
    override fun getScoringType(): ScoringType = ScoringType.ADVANTAGE
}
