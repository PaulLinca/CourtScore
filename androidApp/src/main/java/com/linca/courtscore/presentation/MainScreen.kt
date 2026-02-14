package com.linca.courtscorewear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.linca.courtscorewear.R
import com.linca.courtscorewear.presentation.theme.BackgroundColor
import com.linca.courtscorewear.presentation.theme.PadelBlue
import com.linca.courtscorewear.presentation.theme.PrimaryTextColor
import com.linca.courtscorewear.presentation.theme.SecondaryTextColor
import com.linca.courtscorewear.presentation.theme.logoStyle

private data class ActionButton(
    val description: String,
    val drawableId: Int,
    val onClick: () -> Unit
)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNewMatch: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val actions = remember(onNewMatch, onSettingsClick) {
        listOf(
            ActionButton(
                description = "Start new match",
                drawableId = R.drawable.raquet,
                onClick = onNewMatch
            ),
            ActionButton(
                description = "Settings",
                drawableId = R.drawable.settings,
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
                text = "Court Score",
                textAlign = TextAlign.Center,
                color = PrimaryTextColor,
                style = logoStyle
            )

            Spacer(modifier = Modifier.height(3.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .height(100.dp)
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
                            .size(80.dp)
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
                            tint = PadelBlue.copy(alpha = 0.7f),
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = currentActionLabel,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = SecondaryTextColor
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
