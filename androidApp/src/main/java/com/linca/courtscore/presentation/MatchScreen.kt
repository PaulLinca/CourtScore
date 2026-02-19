package com.linca.courtscore.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.linca.courtscore.presentation.theme.BackgroundColor
import com.linca.courtscore.presentation.theme.ColorScheme
import com.linca.courtscore.presentation.theme.ElevatedBackgroundColor
import com.linca.courtscore.presentation.theme.LocalColorScheme
import com.linca.courtscore.presentation.theme.PadelBlue
import com.linca.courtscore.presentation.theme.PrimaryTextColor
import com.linca.courtscore.presentation.theme.SecondaryTextColor
import com.linca.courtscorewear.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

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
    colorScheme: ColorScheme,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState.gameWinner) {
        if (uiState.gameWinner != null) {
            delay(1500)
            viewModel.onAnimationComplete()
        }
    }
    LaunchedEffect(uiState.setWinner) {
        if (uiState.setWinner != null) {
            delay(2000)
            viewModel.onSetAnimationComplete()
        }
    }

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
                gameWinner = uiState.gameWinner,
                onAnimationComplete = viewModel::onAnimationComplete,
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
                        contentDescription = stringResource(R.string.undo),
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
                        contentDescription = stringResource(R.string.finish),
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
                        text = stringResource(R.string.finish_dialog_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.finish),
                        contentDescription = stringResource(R.string.finish),
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
                        Text(stringResource(R.string.no), color = PrimaryTextColor, style = MaterialTheme.typography.body2)
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
                            stringResource(R.string.yes),
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
                        text = stringResource(R.string.back_dialog_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.undo),
                        contentDescription = stringResource(R.string.undo),
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
                        Text(stringResource(R.string.no), color = PrimaryTextColor, style = MaterialTheme.typography.body2)
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
                            stringResource(R.string.yes),
                            color = PrimaryTextColor,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            )
        }

        // Subtle Set Winner Animation Overlay
        AnimatedVisibility(
            visible = uiState.setWinner != null,
            enter = scaleIn(
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                initialScale = 0.8f
            ) + fadeIn(animationSpec = tween(durationMillis = 400)),
            exit = fadeOut(animationSpec = tween(durationMillis = 400))
        ) {
            val animationColor = when (uiState.setWinner) {
                1 -> colorScheme.playerOneColor
                2 -> colorScheme.playerTwoColor
                else -> PrimaryTextColor
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.tennis_ball_filled),
                        contentDescription = stringResource(R.string.set_won_description),
                        tint = animationColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.set_won),
                        style = MaterialTheme.typography.title2,
                        color = animationColor
                    )
                }
            }
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
    gameWinner: Int? = null,
    onAnimationComplete: () -> Unit = {},
    modifier: Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    // Adjust vertical padding based on screen size
    val verticalPadding = when {
        screenWidthDp < 210 -> 0.dp
        else -> 10.dp
    }

    Row(
        modifier = modifier
            .padding(vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GameScoreButton(
            score = player1GameScore,
            onClick = onPlayer1Score,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            primaryColor = playerOneColor,
            showWinAnimation = gameWinner != null,
            onAnimationComplete = onAnimationComplete
        )

        GameScoreButton(
            score = player2GameScore,
            onClick = onPlayer2Score,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            primaryColor = playerTwoColor,
            showWinAnimation = gameWinner != null,
            onAnimationComplete = onAnimationComplete
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
    enabled: Boolean = true,
    showWinAnimation: Boolean = false,
    onAnimationComplete: () -> Unit = {}
) {
    val shouldAnimate = showWinAnimation && score == "0"

    LaunchedEffect(showWinAnimation) {
        if (showWinAnimation) {
            delay(700)
            onAnimationComplete()
        }
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxSize(),
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
            AnimatedContent(
                targetState = if (shouldAnimate) "${score}_${System.currentTimeMillis()}" else score,
                transitionSpec = {
                    if (shouldAnimate) {
                        (slideInVertically(
                            initialOffsetY = { -it }, // Start from above
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(animationSpec = tween(durationMillis = 400)))
                            .togetherWith(
                                slideOutVertically(
                                    targetOffsetY = { it }, // Exit downward
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutLinearInEasing
                                    )
                                ) + fadeOut(animationSpec = tween(durationMillis = 300))
                            )
                    } else {
                        fadeIn(animationSpec = tween(0))
                            .togetherWith(fadeOut(animationSpec = tween(0)))
                    }
                },
                label = "score_animation"
            ) { targetState ->
                // Extract actual score (remove timestamp if present)
                val displayScore = if (targetState.contains("_")) {
                    targetState.substringBefore("_")
                } else {
                    targetState
                }
                Text(
                    text = displayScore,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) textColor else textColor.copy(alpha = 0.3f)
                )
            }
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
                contentDescription = stringResource(R.string.player_one_serving),
                tint = if (isPlayerOneServing)
                    (if (enabled) playerOneColor else playerOneColor.copy(alpha = 0.3f))
                else
                    SecondaryTextColor.copy(alpha = 0.3f),
                modifier = Modifier.size(10.dp)
            )

            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.tennis_ball),
                contentDescription = stringResource(R.string.player_two_serving),
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
                    contentDescription = stringResource(R.string.player_one_won),
                    tint = playerOneColor,
                    modifier = Modifier.size(15.dp)
                )
            }

            playerTwoWon -> {
                Icon(
                    painter = painterResource(R.drawable.winner),
                    contentDescription = stringResource(R.string.player_two_won),
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


