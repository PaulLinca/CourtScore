package com.linca.courtscore.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.linca.courtscore.presentation.theme.BackgroundColor
import com.linca.courtscore.presentation.theme.PadelBlue
import com.linca.courtscore.presentation.theme.PrimaryTextColor
import com.linca.courtscore.presentation.theme.SecondaryTextColor
import com.linca.courtscore.presentation.theme.logoStyle
import com.linca.courtscorewear.R

private data class ActionButton(
    val description: String,
    val drawableId: Int,
    val onClick: () -> Unit
)

@Composable
private fun PagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        repeat(pageCount) { page ->
            val isSelected = pagerState.currentPage == page
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = if (isSelected) PrimaryTextColor else SecondaryTextColor.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNewMatch: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val newMatchLabel = stringResource(R.string.new_match)
    val settingsLabel = stringResource(R.string.settings)
    val appTitle = stringResource(R.string.app_title)

    val actions = remember(onNewMatch, onSettingsClick, newMatchLabel, settingsLabel) {
        listOf(
            ActionButton(
                description = newMatchLabel,
                drawableId = R.drawable.tennis_ball_filled,
                onClick = onNewMatch
            ),
            ActionButton(
                description = settingsLabel,
                drawableId = R.drawable.settings_filled,
                onClick = onSettingsClick
            )
        )
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { actions.size })
    val currentActionLabel by remember(actions, pagerState) {
        derivedStateOf {
            actions.getOrNull(pagerState.currentPage)?.description ?: ""
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = appTitle,
                textAlign = TextAlign.Center,
                color = PrimaryTextColor,
                style = logoStyle
            )

            Spacer(modifier = Modifier.height(3.dp))


            val configuration = LocalConfiguration.current
            val screenWidthDp = configuration.screenWidthDp

            // Adjust height based on screen size
            val buttonHeight = when {
                screenWidthDp < 210 -> 100.dp
                else -> 130.dp
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .height(height = buttonHeight)
                    .fillMaxSize(),
                pageSpacing = 12.dp
            ) { pageIndex ->
                val action = actions[pageIndex]
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = action.onClick,
                        modifier = Modifier
                            .size(buttonHeight - 20.dp)
                            .border(
                                width = 2.dp,
                                color = PadelBlue,
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            painter = painterResource(action.drawableId),
                            contentDescription = null,
                            tint = PadelBlue,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }

            PagerIndicator(
                pagerState = pagerState,
                pageCount = actions.size
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = currentActionLabel,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = PrimaryTextColor
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
