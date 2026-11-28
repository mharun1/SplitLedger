package com.harun.splitledger.data.repository

import com.harun.splitledger.data.local.prefs.UserPreferencesDataStore
import com.harun.splitledger.domain.model.AppThemeMode
import com.harun.splitledger.domain.model.UserPreferences
import com.harun.splitledger.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
): UserPreferencesRepository {
    override fun observeUserPreferences(): Flow<UserPreferences> {
        return userPreferencesDataStore.preferences
    }

    override suspend fun setHomeCurrency(currencyCode: String) {
        userPreferencesDataStore.setDefaultHomeCurrency(currencyCode)
    }

    override suspend fun setAppTheme(themeMode: AppThemeMode) {
        userPreferencesDataStore.setThemeMode(themeMode)
    }
}