package com.harun.splitledger.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.harun.splitledger.domain.model.AppThemeMode
import com.harun.splitledger.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "splitledger_prefs")

class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKey {
        val DEFAULT_HOME_CURRENCY = stringPreferencesKey("default_home_currency")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LAST_FX_SYNC_EPOCH_MS = longPreferencesKey("last_fx_sync_epoch_ms")
        val HAS_SEEDED_DEMO = booleanPreferencesKey("has_seeded_demo")
    }

    val preferences: Flow<UserPreferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { preferences ->
            UserPreferences(
                defaultHomeCurrency = preferences[PreferencesKey.DEFAULT_HOME_CURRENCY] ?: "USD",
                themeMode = preferences[PreferencesKey.THEME_MODE]?.let { runCatching { AppThemeMode.valueOf(it) }.getOrNull() } ?: AppThemeMode.SYSTEM,
                lastFxSyncEpochMs = preferences[PreferencesKey.LAST_FX_SYNC_EPOCH_MS],
                hasSeededDemo = preferences[PreferencesKey.HAS_SEEDED_DEMO] ?: false
            )
        }

    suspend fun setDefaultHomeCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.DEFAULT_HOME_CURRENCY] = currencyCode.uppercase()
        }
    }

    suspend fun setThemeMode(themeMode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setLastFxSyncEpochMs(epochMs: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.LAST_FX_SYNC_EPOCH_MS] = epochMs
        }
    }

    suspend fun setHasSeededDemo(seeded: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.HAS_SEEDED_DEMO] = seeded
        }
    }
}
