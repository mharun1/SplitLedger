package com.harun.splitledger.data.worker

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.harun.splitledger.domain.model.FxSyncState
import com.harun.splitledger.domain.repository.FxSyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerFxSyncScheduler @Inject constructor(
    private val workManager: WorkManager
): FxSyncScheduler {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun schedulePeriodicSync() {
        val periodicRequest = PeriodicWorkRequestBuilder<FxSyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    override fun syncNow() {
        val oneTimeRequest = OneTimeWorkRequestBuilder<FxSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeRequest
        )
    }

    override fun observeSyncStatus(): Flow<FxSyncState> {
        return workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_ONE_TIME_WORK_NAME)
            .map { workInfo ->
                val info = workInfo.firstOrNull()

                if (info == null) FxSyncState.Idle
                else {
                    when (info.state) {
                        WorkInfo.State.RUNNING -> FxSyncState.Syncing
                        WorkInfo.State.SUCCEEDED -> {
                            val epochMs = info.outputData.getLong(FxSyncWorker.KEY_SYNCED_AT_EPOCH_MS, System.currentTimeMillis())
                            FxSyncState.Synced(epochMs)
                        }
                        WorkInfo.State.FAILED -> {
                            val error = info.outputData.getString(FxSyncWorker.KEY_ERROR_MESSAGE) ?: "Sync failed"
                            FxSyncState.Error(error)
                        }
                        WorkInfo.State.ENQUEUED,
                        WorkInfo.State.BLOCKED,
                        WorkInfo.State.CANCELLED -> FxSyncState.Idle
                    }
                }
            }
    }

    companion object {
        private const val UNIQUE_PERIODIC_WORKER_NAME = "fx_periodic_sync"
        private const val UNIQUE_ONE_TIME_WORK_NAME = "fx_one_time_sync"
    }
}