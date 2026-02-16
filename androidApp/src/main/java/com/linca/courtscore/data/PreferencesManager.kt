package com.linca.courtscore.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.linca.courtscore.presentation.theme.ColorSchemes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val COLOR_SCHEME_KEY = stringPreferencesKey("color_scheme")
    }

    val colorSchemeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[COLOR_SCHEME_KEY] ?: ColorSchemes.TealCoral.name
        }

    suspend fun saveColorScheme(schemeName: String) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_SCHEME_KEY] = schemeName
        }
    }
}
