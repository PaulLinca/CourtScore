package com.linca.courtscorewear.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.SwipeToDismissValue
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import kotlinx.coroutines.flow.collectLatest
import com.linca.courtscore.presentation.MatchUiState
import com.linca.courtscore.presentation.MatchViewModel
import com.linca.courtscorewear.R
import com.linca.courtscorewear.presentation.theme.BackgroundColor
import com.linca.courtscorewear.presentation.theme.ElevatedBackgroundColor
import com.linca.courtscorewear.presentation.theme.LocalColorScheme
import com.linca.courtscorewear.presentation.theme.PadelBlue
import com.linca.courtscorewear.presentation.theme.PrimaryTextColor
import com.linca.courtscorewear.presentation.theme.SecondaryTextColor

@Composable
fun MatchScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchViewModel = remember { MatchViewModel() },
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = LocalColorScheme.current

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    LaunchedEffect(swipeToDismissBoxState) {
        snapshotFlow { swipeToDismissBoxState.currentValue }
            .collectLatest { value ->
                if (value == SwipeToDismissValue.Dismissed) {
                    if (!uiState.isFinished) {
                        viewModel.onBackPressed()
                        swipeToDismissBoxState.snapTo(SwipeToDismissValue.Default)
                    } else {
                        onNavigateBack()
                    }
                }
            }
    }

    BackHandler {
        val isMatchFinished = viewModel.onBackPressed()
        if (!isMatchFinished) {
            onNavigateBack()
        }
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        onDismissed = { /* handled in LaunchedEffect */ },
        modifier = modifier
    ) { isBackground ->
        if (!isBackground) {
            MatchScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                colorScheme = colorScheme,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun MatchScreenContent(
    uiState: MatchUiState,
    viewModel: MatchViewModel,
    colorScheme: com.linca.courtscorewear.presentation.theme.ColorScheme,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))

                ServingIndicator(
                    isPlayerOneServing = uiState.playerOneServing,
                    onToggleServing = viewModel::toggleServing,
                    enabled = !uiState.isFinished,
                    playerOneColor = colorScheme.playerOneColor,
                    playerTwoColor = colorScheme.playerTwoColor
                )

                ScoreTable(
                    player1SetScores = uiState.playerOneSetScores,
                    player2SetScores = uiState.playerTwoSetScores,
                    playerOneColor = colorScheme.playerOneColor,
                    playerTwoColor = colorScheme.playerTwoColor
                )

                WinnerIndicator(
                    playerOneWon = uiState.playerOneWon,
                    playerTwoWon = uiState.playerTwoWon,
                    playerOneColor = colorScheme.playerOneColor,
                    playerTwoColor = colorScheme.playerTwoColor
                )

                Spacer(Modifier.weight(1f))
            }

            CurrentGameScore(
                player1GameScore = uiState.playerOneGameScore,
                player2GameScore = uiState.playerTwoGameScore,
                onPlayer1Score = viewModel::onPlayerOneScored,
                onPlayer2Score = viewModel::onPlayerTwoScored,
                enabled = !uiState.isFinished,
                playerOneColor = colorScheme.playerOneColor,
                playerTwoColor = colorScheme.playerTwoColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )

            Row {
                Button(
                    onClick = viewModel::onUndo,
                    enabled = !uiState.isFinished,
                    modifier = Modifier.size(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ElevatedBackgroundColor,
                        disabledBackgroundColor = ElevatedBackgroundColor.copy(alpha = 0.3f)
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(R.drawable.undo),
                        contentDescription = "Undo",
                        tint = if (uiState.isFinished) SecondaryTextColor.copy(alpha = 0.3f) else SecondaryTextColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp)
                    )
                }

                Spacer(Modifier.size(10.dp))

                Button(
                    onClick = viewModel::onFinishClicked,
                    enabled = !uiState.isFinished,
                    modifier = Modifier.size(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ElevatedBackgroundColor,
                        disabledBackgroundColor = ElevatedBackgroundColor.copy(alpha = 0.3f)
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(R.drawable.finish),
                        contentDescription = "Finish",
                        tint = if (uiState.isFinished) SecondaryTextColor.copy(alpha = 0.3f) else SecondaryTextColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp)
                    )
                }
            }
        }

        if (uiState.showFinishDialog) {
            Alert(
                backgroundColor = BackgroundColor,
                title = {
                    Text(
                        text = "Are you sure you want to end the match?",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.finish),
                        contentDescription = "Finish",
                        tint = SecondaryTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                negativeButton = {
                    Button(
                        onClick = viewModel::onFinishCancelled,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ElevatedBackgroundColor
                        )
                    ) {
                        Text("No", color = PrimaryTextColor, style = MaterialTheme.typography.body2)
                    }
                },
                positiveButton = {
                    Button(
                        onClick = viewModel::onFinishConfirmed,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = PadelBlue
                        )
                    ) {
                        Text(
                            "Yes",
                            color = PrimaryTextColor,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            )
        }

        if (uiState.showBackDialog) {
            Alert(
                backgroundColor = BackgroundColor,
                title = {
                    Text(
                        text = "Are you sure you want to leave the match?",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.undo),
                        contentDescription = "Back",
                        tint = SecondaryTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                negativeButton = {
                    Button(
                        onClick = viewModel::onBackCancelled,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ElevatedBackgroundColor
                        )
                    ) {
                        Text("No", color = PrimaryTextColor, style = MaterialTheme.typography.body2)
                    }
                },
                positiveButton = {
                    Button(
                        onClick = {
                            viewModel.onBackConfirmed()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = PadelBlue
                        )
                    ) {
                        Text(
                            "Yes",
                            color = PrimaryTextColor,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CurrentGameScore(
    player1GameScore: String,
    player2GameScore: String,
    onPlayer1Score: () -> Unit,
    onPlayer2Score: () -> Unit,
    enabled: Boolean = true,
    playerOneColor: Color,
    playerTwoColor: Color,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GameScoreButton(
            score = player1GameScore,
            onClick = onPlayer1Score,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            primaryColor = playerOneColor
        )

        GameScoreButton(
            score = player2GameScore,
            onClick = onPlayer2Score,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            primaryColor = playerTwoColor
        )
    }
}

@Composable
fun ScoreTable(
    player1SetScores: List<Int>,
    player2SetScores: List<Int>,
    playerOneColor: Color,
    playerTwoColor: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            player1SetScores.forEach { setScore ->
                Text(
                    text = setScore.toString(),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = playerOneColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            player2SetScores.forEach { setScore ->
                Text(
                    text = setScore.toString(),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = playerTwoColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun GameScoreButton(
    score: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color,
    textColor: Color = primaryColor,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = if (enabled) primaryColor else primaryColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) textColor else textColor.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ServingIndicator(
    isPlayerOneServing: Boolean,
    onToggleServing: () -> Unit,
    enabled: Boolean = true,
    playerOneColor: Color,
    playerTwoColor: Color
) {
    Button(
        onClick = onToggleServing,
        enabled = enabled,
        modifier = Modifier.size(24.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        ),
        shape = CircleShape
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.tennis_ball),
                contentDescription = "Player one serving",
                tint = if (isPlayerOneServing)
                    (if (enabled) playerOneColor else playerOneColor.copy(alpha = 0.3f))
                else
                    SecondaryTextColor.copy(alpha = 0.3f),
                modifier = Modifier.size(10.dp)
            )

            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.tennis_ball),
                contentDescription = "Player two serving",
                tint = if (!isPlayerOneServing)
                    (if (enabled) playerTwoColor else playerTwoColor.copy(alpha = 0.3f))
                else
                    SecondaryTextColor.copy(alpha = 0.3f),
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@Composable
fun WinnerIndicator(
    playerOneWon: Boolean,
    playerTwoWon: Boolean,
    playerOneColor: Color,
    playerTwoColor: Color
) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            playerOneWon -> {
                Icon(
                    painter = painterResource(R.drawable.winner),
                    contentDescription = "Player one won",
                    tint = playerOneColor,
                    modifier = Modifier.size(15.dp)
                )
            }

            playerTwoWon -> {
                Icon(
                    painter = painterResource(R.drawable.winner),
                    contentDescription = "Player two won",
                    tint = playerTwoColor,
                    modifier = Modifier.size(15.dp)
                )
            }

            else -> {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}


