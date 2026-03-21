package com.linca.courtscore.engine

import com.linca.courtscore.domain.model.ScoringType

/**
 * Result of a single scoring action. Carries animation hints to the ViewModel so
 * no sport-specific logic leaks into presentation code.
 */
data class ScoringResult(
    /** Player (1 or 2) who just won a scoring unit (game in padel, goal in football). */
    val scoringUnitWinner: Int? = null,
    /** Player (1 or 2) who just won a period (set in padel, half in football). */
    val periodWinner: Int? = null,
    /** Player (1 or 2) who won at golden/star point deuce. Padel-specific, null for other sports. */
    val goldenPointWinner: Int? = null
)

/**
 * Sport-neutral display data produced by a strategy. The ViewModel maps this onto
 * the existing [com.linca.courtscore.presentation.MatchUiState] so the UI layer
 * requires no sport-specific knowledge.
 */
data class SportDisplayData(
    val playerOneMainScore: String,
    val playerTwoMainScore: String,
    val playerOneSubScores: List<Int>,
    val playerTwoSubScores: List<Int>,
    val playerOneWins: Boolean,
    val playerTwoWins: Boolean,
    val isFinished: Boolean,
    /** True when the strategy needs the user to choose a configuration option (e.g. padel deuce rule). */
    val showConfigDialog: Boolean = false,
    /** Passed through to MatchUiState.scoringType; unused for sports that don't have this concept. */
    val scoringType: ScoringType = ScoringType.ADVANTAGE,
    /** Whether to show the sub-scores table (sets in padel, periods in other sports). */
    val showSubScores: Boolean = true,
    /** Whether to show the serving indicator. */
    val showServingIndicator: Boolean = true,
    /**
     * When non-null, overrides the ViewModel's serve-side tracking. Use this for sports
     * (e.g. badminton) where serve side is determined by a rule rather than positional alternation.
     * Null means "let the ViewModel manage serve side as usual."
     */
    val serveFromRight: Boolean? = null,
    /**
     * When non-null, overrides the ViewModel's server tracking. Use this for sports
     * (e.g. badminton) where the server is always the point winner rather than alternating.
     * Null means "let the ViewModel manage the current server as usual."
     */
    val playerOneServing: Boolean? = null
)

/**
 * Encapsulates all sport-specific scoring logic. Implement this interface once per sport;
 * the MatchViewModel interacts only with this contract.
 *
 * Implementations are stateful: they own their internal game state and history for undo.
 */
interface SportStrategy {
    fun pointForPlayerOne(): ScoringResult
    fun pointForPlayerTwo(): ScoringResult
    fun undo(): ScoringResult
    fun finishMatch()
    fun isFinished(): Boolean
    fun toDisplayData(): SportDisplayData

    // Padel-specific configuration. No-ops for sports that don't use this.
    fun setScoringType(type: ScoringType)
    fun getScoringType(): ScoringType
}
