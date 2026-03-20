package com.linca.courtscore.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.linca.courtscore.domain.model.Sport
import com.linca.courtscore.presentation.theme.BackgroundColor
import com.linca.courtscore.presentation.theme.ElevatedBackgroundColor
import com.linca.courtscore.presentation.theme.PrimaryTextColor
import com.linca.courtscorewear.R

@Composable
fun SportSelectScreen(
    modifier: Modifier = Modifier,
    onSportSelected: (Sport) -> Unit
) {
    val listState = rememberScalingLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(R.string.select_sport),
                    style = MaterialTheme.typography.body2,
                    color = PrimaryTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            item {
                Chip(
                    onClick = { onSportSelected(Sport.PADEL) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    colors = ChipDefaults.chipColors(backgroundColor = ElevatedBackgroundColor),
                    label = {
                        Text(
                            text = stringResource(R.string.sport_padel),
                            style = MaterialTheme.typography.body2,
                            color = PrimaryTextColor
                        )
                    }
                )
            }
            item {
                Chip(
                    onClick = { onSportSelected(Sport.FOOTBALL) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    colors = ChipDefaults.chipColors(backgroundColor = ElevatedBackgroundColor),
                    label = {
                        Text(
                            text = stringResource(R.string.sport_football),
                            style = MaterialTheme.typography.body2,
                            color = PrimaryTextColor
                        )
                    }
                )
            }
        }
    }
}
