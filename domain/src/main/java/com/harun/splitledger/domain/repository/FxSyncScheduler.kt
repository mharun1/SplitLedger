package com.harun.splitledger.domain.repository

import com.harun.splitledger.domain.model.FxSyncState
import kotlinx.coroutines.flow.Flow

interface FxSyncScheduler {
    fun schedulePeriodicSync()
    fun syncNow()
    fun observeSyncStatus(): Flow<FxSyncState>
}