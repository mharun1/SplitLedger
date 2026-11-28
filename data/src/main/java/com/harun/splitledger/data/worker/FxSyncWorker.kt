package com.harun.splitledger.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.harun.splitledger.data.local.db.dao.FxRateDao
import com.harun.splitledger.data.local.db.entity.FxRateEntity
import com.harun.splitledger.data.local.prefs.UserPreferencesDataStore
import com.harun.splitledger.data.remote.FrankfurterApi
import com.harun.splitledger.domain.model.CurrencyCatalog
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal

@HiltWorker
class FxSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val frankfurterApi: FrankfurterApi,
    private val fxRateDao: FxRateDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val nowEpochMs = System.currentTimeMillis()
            val currencies = CurrencyCatalog.currencies.map { it.code }.toSet()
            val apiFiltered = frankfurterApi.getRates("EUR")
                .filter {
                    it.base.equals("EUR", ignoreCase = true) &&
                    it.quote.uppercase() in currencies &&
                    it.rate.isFinite() &&
                    it.rate > 0.0
                }

            if (apiFiltered.isEmpty()) return Result.failure(workDataOf(KEY_ERROR_MESSAGE to "No supported rates found in response."))

            val entities = apiFiltered.map { dto ->
                val rate = BigDecimal.valueOf(dto.rate).stripTrailingZeros().toPlainString()
                FxRateEntity(
                    quoteCurrency = dto.quote.uppercase(),
                    baseCurrency = dto.base,
                    rateDecimal = rate,
                    updatedAtEpochMs = nowEpochMs,
                    isBundledFallback = false
                )
            } + FxRateEntity(
                quoteCurrency = "EUR",
                baseCurrency = "EUR",
                rateDecimal = "1",
                updatedAtEpochMs = nowEpochMs,
                isBundledFallback = false
            )

            fxRateDao.upsertRates(entities)
            userPreferencesDataStore.setLastFxSyncEpochMs(nowEpochMs)
            return Result.success(workDataOf(KEY_SYNCED_AT_EPOCH_MS to nowEpochMs))
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            return Result.retry()
        } catch (e: HttpException) {
            if (e.code() in 500..599 || e.code() == 429) return Result.retry()
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to "HTTP ${e.code()}: ${e.message()}"))
        } catch (e: SerializationException) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to "Malformed JSON: ${e.message}"))
        }
    }

    companion object {
        const val KEY_SYNCED_AT_EPOCH_MS = "synced_at_epoch_ms"
        const val KEY_ERROR_MESSAGE = "error_message"

    }
}