package com.linca.courtscore.presentation

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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.linca.courtscore.data.PreferencesManager
import com.linca.courtscore.presentation.theme.BackgroundColor
import com.linca.courtscore.presentation.theme.ColorScheme
import com.linca.courtscore.presentation.theme.ColorSchemes
import com.linca.courtscore.presentation.theme.ElevatedBackgroundColor
import com.linca.courtscore.presentation.theme.PadelBlue
import com.linca.courtscore.presentation.theme.PrimaryTextColor
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    onNavigateBack: () -> Boolean
) {
    val currentColorSchemeName by preferencesManager.colorSchemeFlow.collectAsState(initial = ColorSchemes.TealCoral.name)
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()

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
                    text = "Color Scheme",
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
                        text = "Done",
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