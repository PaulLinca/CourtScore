package com.linca.courtscore.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.linca.courtscore.domain.model.ScoringType
import com.linca.courtscore.presentation.theme.ColorSchemes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val COLOR_SCHEME_KEY = stringPreferencesKey("color_scheme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val SCORING_TYPE_KEY = stringPreferencesKey("scoring_type")
    }

    val colorSchemeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[COLOR_SCHEME_KEY] ?: ColorSchemes.SunsetOcean.name
        }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "system"
        }

    val scoringTypeFlow: Flow<ScoringType> = context.dataStore.data
        .map { preferences ->
            when (preferences[SCORING_TYPE_KEY]) {
                "GOLDEN_POINT" -> ScoringType.GOLDEN_POINT
                "STAR_POINT" -> ScoringType.STAR_POINT
                else -> ScoringType.ADVANTAGE
            }
        }

    suspend fun saveColorScheme(schemeName: String) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_SCHEME_KEY] = schemeName
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun saveScoringType(scoringType: ScoringType) {
        context.dataStore.edit { preferences ->
            preferences[SCORING_TYPE_KEY] = scoringType.name
        }
    }
}
