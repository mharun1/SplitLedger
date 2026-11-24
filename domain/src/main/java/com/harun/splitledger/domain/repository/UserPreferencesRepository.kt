package com.harun.splitledger.domain.repository

import com.harun.splitledger.domain.model.AppThemeMode
import com.harun.splitledger.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeUserPreferences(): Flow<UserPreferences>
    suspend fun setHomeCurrency(currencyCode: String)
    suspend fun setAppTheme(themeMode: AppThemeMode)
}