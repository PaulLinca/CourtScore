package com.linca.courtscore.engine

import com.linca.courtscore.domain.model.ScoringType

/**
 * Badminton scoring strategy.
 *
 * Rules:
 * - Best of 3 games. First side to win 2 games wins the match.
 * - A game is won by the first side to reach 21 points.
 * - At 20-20, a side must win by 2 clear points.
 * - At 29-29, the first to reach 30 wins the game.
 * - A point is scored on every rally. The winning side serves next.
 * - Server's score parity determines serve side: even score → right court, odd → left court.
 */
class BadmintonStrategy : SportStrategy {

    private data class GameScore(val p1: Int, val p2: Int)

    private data class State(
        val p1Points: Int = 0,
        val p2Points: Int = 0,
        val completedGames: List<GameScore> = emptyList(),
        val isFinished: Boolean = false,
        /** Tracks who is currently serving so serve side can be derived from their score. */
        val serverIsPlayerOne: Boolean = true
    )

    private var state = State()
    private val history = mutableListOf<State>()

    override fun pointForPlayerOne(): ScoringResult = scorePoint(player = 1)

    override fun pointForPlayerTwo(): ScoringResult = scorePoint(player = 2)

    private fun scorePoint(player: Int): ScoringResult {
        if (state.isFinished) return ScoringResult()

        history.add(state)

        val newP1 = state.p1Points + if (player == 1) 1 else 0
        val newP2 = state.p2Points + if (player == 2) 1 else 0

        val winner = resolveGameWinner(newP1, newP2)
        return if (winner != null) {
            val completed = state.completedGames + GameScore(newP1, newP2)
            val p1Games = completed.count { it.p1 > it.p2 }
            val p2Games = completed.count { it.p2 > it.p1 }
            // New game resets to 0-0; game winner serves first (score 0 = even → right court).
            state = State(
                completedGames = completed,
                isFinished = p1Games == 2 || p2Games == 2,
                serverIsPlayerOne = winner == 1
            )
            ScoringResult(scoringUnitWinner = winner, periodWinner = winner)
        } else {
            // Rally point: winner serves next. Serve side is derived from server's new score.
            state = state.copy(
                p1Points = newP1,
                p2Points = newP2,
                serverIsPlayerOne = player == 1
            )
            ScoringResult(scoringUnitWinner = player)
        }
    }

    /**
     * Returns the winner of a game (1 or 2), or null if the game is still in progress.
     * At 20-20 a 2-point lead is required; at 29-29 the first to 30 wins.
     */
    private fun resolveGameWinner(p1: Int, p2: Int): Int? = when {
        p1 >= 21 && p1 - p2 >= 2 -> 1
        p2 >= 21 && p2 - p1 >= 2 -> 2
        p1 == 30 -> 1
        p2 == 30 -> 2
        else -> null
    }

    override fun undo(): ScoringResult {
        if (history.isEmpty()) return ScoringResult()
        val previous = history.removeAt(history.lastIndex)
        val wasGameWin = previous.completedGames.size != state.completedGames.size
        state = previous
        // The strategy owns serving state for badminton, so no scoringUnitWinner needed here;
        // the ViewModel will read the correct server and side from the next toDisplayData() call.
        return ScoringResult(periodWinner = if (wasGameWin) 1 else null)
    }

    override fun finishMatch() {
        state = state.copy(isFinished = true)
    }

    override fun isFinished(): Boolean = state.isFinished

    override fun toDisplayData(): SportDisplayData {
        val padding = List(3) { 0 }
        val subP1 = (state.completedGames.map { it.p1 } + padding).take(3)
        val subP2 = (state.completedGames.map { it.p2 } + padding).take(3)
        val p1Games = state.completedGames.count { it.p1 > it.p2 }
        val p2Games = state.completedGames.count { it.p2 > it.p1 }

        // Serve side: server's score parity — even → right court, odd → left court.
        val serverScore = if (state.serverIsPlayerOne) state.p1Points else state.p2Points
        val serveRight = serverScore % 2 == 0

        return SportDisplayData(
            playerOneMainScore = state.p1Points.toString(),
            playerTwoMainScore = state.p2Points.toString(),
            playerOneSubScores = subP1,
            playerTwoSubScores = subP2,
            playerOneWins = state.isFinished && p1Games > p2Games,
            playerTwoWins = state.isFinished && p2Games > p1Games,
            isFinished = state.isFinished,
            showSubScores = true,
            showServingIndicator = true,
            serveFromRight = serveRight,
            playerOneServing = state.serverIsPlayerOne
        )
    }

    // Badminton has no scoring-type configuration.
    override fun setScoringType(type: ScoringType) = Unit
    override fun getScoringType(): ScoringType = ScoringType.ADVANTAGE
}
