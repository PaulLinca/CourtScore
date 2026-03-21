package com.linca.courtscore.presentation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.linca.courtscore.data.PreferencesManager
import com.linca.courtscore.domain.model.ScoringType
import com.linca.courtscore.domain.model.Sport
import com.linca.courtscore.presentation.theme.BackgroundColor
import com.linca.courtscore.presentation.theme.ColorScheme
import com.linca.courtscore.presentation.theme.ColorSchemes
import com.linca.courtscore.presentation.theme.ElevatedBackgroundColor
import com.linca.courtscore.presentation.theme.PadelBlue
import com.linca.courtscore.presentation.theme.PrimaryTextColor
import com.linca.courtscorewear.R
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    onNavigateBack: () -> Boolean
) {
    val currentColorSchemeName by preferencesManager.colorSchemeFlow.collectAsState(initial = ColorSchemes.SunsetOcean.name)
    val currentLanguage by preferencesManager.languageFlow.collectAsState(initial = "system")
    val currentScoringType by preferencesManager.scoringTypeFlow.collectAsState(initial = null)
    val enabledSports by preferencesManager.enabledSportsFlow.collectAsState(initial = Sport.values().toSet())
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
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
                    text = stringResource(R.string.sports),
                    style = MaterialTheme.typography.title3,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(Sport.values().toList()) { sport ->
                val sportName = when (sport) {
                    Sport.PADEL -> stringResource(R.string.sport_padel)
                    Sport.FOOTBALL -> stringResource(R.string.sport_football)
                    Sport.BADMINTON -> stringResource(R.string.sport_badminton)
                }
                SportToggleCard(
                    sportName = sportName,
                    isEnabled = sport in enabledSports,
                    onToggle = {
                        coroutineScope.launch {
                            val updated = if (sport in enabledSports) {
                                if (enabledSports.size > 1) enabledSports - sport else enabledSports
                            } else {
                                enabledSports + sport
                            }
                            preferencesManager.saveEnabledSports(updated)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.title3,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                LanguageCard(
                    languageName = stringResource(R.string.english),
                    drawableId = R.drawable.gbr,
                    languageCode = "en",
                    isSelected = currentLanguage == "en",
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveLanguage("en")
                            (context as? ComponentActivity)?.recreate()
                        }
                    }
                )
            }

            item {
                LanguageCard(
                    languageName = stringResource(R.string.spanish),
                    languageCode = "es",
                    drawableId = R.drawable.spain,
                    isSelected = currentLanguage == "es",
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveLanguage("es")
                            (context as? ComponentActivity)?.recreate()
                        }
                    }
                )
            }

            item {
                LanguageCard(
                    languageName = stringResource(R.string.catalan),
                    languageCode = "ca",
                    drawableId = R.drawable.catalonia,
                    isSelected = currentLanguage == "ca",
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveLanguage("ca")
                            (context as? ComponentActivity)?.recreate()
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.scoring_type),
                    style = MaterialTheme.typography.title3,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                ScoringTypeCard(
                    typeName = stringResource(R.string.scoring_ask_every_time),
                    description = stringResource(R.string.scoring_ask_every_time_desc),
                    isSelected = currentScoringType == null,
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveScoringType(null)
                        }
                    }
                )
            }

            item {
                ScoringTypeCard(
                    typeName = stringResource(R.string.scoring_advantage),
                    description = stringResource(R.string.scoring_advantage_desc),
                    isSelected = currentScoringType == ScoringType.ADVANTAGE,
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveScoringType(ScoringType.ADVANTAGE)
                        }
                    }
                )
            }

            item {
                ScoringTypeCard(
                    typeName = stringResource(R.string.scoring_golden_point),
                    description = stringResource(R.string.scoring_golden_point_desc),
                    isSelected = currentScoringType == ScoringType.GOLDEN_POINT,
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveScoringType(ScoringType.GOLDEN_POINT)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.color_scheme),
                    style = MaterialTheme.typography.title3,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(ColorSchemes.all) { scheme ->
                ColorSchemeCard(
                    colorScheme = scheme,
                    isSelected = scheme.name == currentColorSchemeName,
                    onSelect = {
                        coroutineScope.launch {
                            preferencesManager.saveColorScheme(scheme.name)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onNavigateBack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PadelBlue
                    )
                ) {
                    Text(
                        text = stringResource(R.string.done),
                        style = MaterialTheme.typography.button,
                        color = PrimaryTextColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ColorSchemeCard(
    colorScheme: ColorScheme,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Chip(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isSelected) colorScheme.playerOneColor.copy(alpha = 0.3f) else ElevatedBackgroundColor
        ),
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(colorScheme.playerOneColor, CircleShape)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryTextColor else colorScheme.playerOneColor,
                                shape = CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(colorScheme.playerTwoColor, CircleShape)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryTextColor else colorScheme.playerTwoColor,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    )
}

@Composable
fun LanguageCard(
    languageName: String,
    @Suppress("UNUSED_PARAMETER") languageCode: String,
    drawableId: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Chip(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isSelected) PadelBlue.copy(alpha = 0.3f) else ElevatedBackgroundColor
        ),
        label = {
            Row {
                Spacer(Modifier.weight(1f))
                Icon(
                    painter = painterResource(drawableId),
                    contentDescription = languageName,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )
                Spacer(Modifier.weight(1f))
            }
        }
    )
}

@Composable
fun SportToggleCard(
    sportName: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Chip(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isEnabled) PadelBlue.copy(alpha = 0.3f) else ElevatedBackgroundColor
        ),
        label = {
            Text(
                text = sportName,
                style = MaterialTheme.typography.body1,
                color = PrimaryTextColor,
                fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Normal
            )
        }
    )
}

@Composable
fun ScoringTypeCard(
    typeName: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Chip(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isSelected) PadelBlue.copy(alpha = 0.3f) else ElevatedBackgroundColor
        ),
        label = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = typeName,
                    style = MaterialTheme.typography.body1,
                    color = PrimaryTextColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.caption2,
                    color = PrimaryTextColor.copy(alpha = 0.7f)
                )
            }
        }
    )
}

