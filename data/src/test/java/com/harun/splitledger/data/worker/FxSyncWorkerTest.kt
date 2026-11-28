package com.harun.splitledger.data.worker

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.harun.splitledger.data.local.db.SplitLedgerDatabase
import com.harun.splitledger.data.local.prefs.UserPreferencesDataStore
import com.harun.splitledger.data.local.seed.BundledFxRates
import com.harun.splitledger.data.remote.FrankfurterApi
import com.harun.splitledger.data.remote.FrankfurterRatesDto
import com.harun.splitledger.data.repository.FxRateRepositoryImpl
import com.harun.splitledger.domain.model.FxRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.io.IOException
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FxSyncWorkerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var database: SplitLedgerDatabase
    private lateinit var server: MockWebServer
    private lateinit var api: FrankfurterApi

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, SplitLedgerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        server = MockWebServer()
        server.start()

        val json = Json { ignoreUnknownKeys = true }
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FrankfurterApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
        database.close()
    }

    private fun createPrefsStore(scope: CoroutineScope): UserPreferencesDataStore {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(tempFolder.newFolder(), "fx_sync_test.preferences_pb") },
        )
        return UserPreferencesDataStore(dataStore)
    }

    private fun buildWorker(
        frankfurterApi: FrankfurterApi,
        prefsStore: UserPreferencesDataStore,
    ): FxSyncWorker {
        val factory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters,
            ): ListenableWorker = FxSyncWorker(
                appContext = appContext,
                workerParams = workerParameters,
                frankfurterApi = frankfurterApi,
                fxRateDao = database.fxRateDao(),
                userPreferencesDataStore = prefsStore,
            )
        }
        return TestListenableWorkerBuilder<FxSyncWorker>(context)
            .setWorkerFactory(factory)
            .build()
    }

    @Test
    fun successfulSyncUpdatesRoomRatesAndDataStoreAndReturnsSuccess() = runTest {
        database.fxRateDao().upsertRates(BundledFxRates.toEntities())
        val prefsStore = createPrefsStore(backgroundScope)

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [
                      {"date":"2026-04-15","base":"EUR","quote":"USD","rate":1.0842},
                      {"date":"2026-04-15","base":"EUR","quote":"JPY","rate":165.42},
                      {"date":"2026-04-15","base":"EUR","quote":"XYZ","rate":99.0}
                    ]
                    """.trimIndent()
                )
        )

        val result = buildWorker(api, prefsStore).doWork()

        assertTrue(result is ListenableWorker.Result.Success)
        val syncedAt = (result as ListenableWorker.Result.Success)
            .outputData
            .getLong(FxSyncWorker.KEY_SYNCED_AT_EPOCH_MS, -1L)
        assertTrue(syncedAt > 0L)

        val ratesByQuote = database.fxRateDao().getRates().associateBy { it.quoteCurrency }
        val eur = requireNotNull(ratesByQuote["EUR"])
        assertEquals("1", eur.rateDecimal)
        assertFalse(eur.isBundledFallback)

        val usd = requireNotNull(ratesByQuote["USD"])
        assertEquals("1.0842", usd.rateDecimal)
        assertFalse(usd.isBundledFallback)
        assertEquals(syncedAt, usd.updatedAtEpochMs)

        val jpy = requireNotNull(ratesByQuote["JPY"])
        assertEquals("165.42", jpy.rateDecimal)
        assertFalse(jpy.isBundledFallback)

        assertNull(ratesByQuote["XYZ"])
        assertEquals(syncedAt, prefsStore.preferences.first().lastFxSyncEpochMs)
    }

    @Test
    fun ioExceptionReturnsRetryAndPreservesCachedRoomRates() = runTest {
        database.fxRateDao().upsertRates(BundledFxRates.toEntities())
        val beforeRates = database.fxRateDao().getRates()
        val prefsStore = createPrefsStore(backgroundScope)

        val failingApi = object : FrankfurterApi {
            override suspend fun getRates(base: String): List<FrankfurterRatesDto> {
                throw IOException("offline")
            }
        }

        val result = buildWorker(failingApi, prefsStore).doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        assertEquals(beforeRates, database.fxRateDao().getRates())
        assertNull(prefsStore.preferences.first().lastFxSyncEpochMs)
    }

    @Test
    fun http503ReturnsRetryAndPreservesCachedRoomRates() = runTest {
        database.fxRateDao().upsertRates(BundledFxRates.toEntities())
        val beforeRates = database.fxRateDao().getRates()
        val prefsStore = createPrefsStore(backgroundScope)

        server.enqueue(MockResponse().setResponseCode(503))

        val result = buildWorker(api, prefsStore).doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        assertEquals(beforeRates, database.fxRateDao().getRates())
        assertNull(prefsStore.preferences.first().lastFxSyncEpochMs)
    }

    @Test
    fun http400ReturnsFailureWithoutCorruptingRoomCache() = runTest {
        database.fxRateDao().upsertRates(BundledFxRates.toEntities())
        val beforeRates = database.fxRateDao().getRates()
        val prefsStore = createPrefsStore(backgroundScope)

        server.enqueue(MockResponse().setResponseCode(400).setBody("Bad Request"))

        val result = buildWorker(api, prefsStore).doWork()

        assertTrue(result is ListenableWorker.Result.Failure)
        val errorMessage = (result as ListenableWorker.Result.Failure)
            .outputData
            .getString(FxSyncWorker.KEY_ERROR_MESSAGE)
        assertNotNull(errorMessage)
        assertEquals(beforeRates, database.fxRateDao().getRates())
        assertNull(prefsStore.preferences.first().lastFxSyncEpochMs)
    }

    @Test
    fun isRateStaleChecksBundledFallbackAnd24HourThreshold() {
        val repo = FxRateRepositoryImpl(database.fxRateDao())
        val now = 2_000_000_000_000L
        val twentyFourHoursMs = 86_400_000L

        val bundledFresh = FxRate(
            quoteCurrency = "USD",
            rateDecimal = BigDecimal("1.08"),
            updatedAtEpochMs = now - 1_000L,
            isBundledFallback = true,
        )
        val syncedOlderThan24h = FxRate(
            quoteCurrency = "USD",
            rateDecimal = BigDecimal("1.08"),
            updatedAtEpochMs = now - twentyFourHoursMs - 1L,
            isBundledFallback = false,
        )
        val syncedAt24h = FxRate(
            quoteCurrency = "USD",
            rateDecimal = BigDecimal("1.08"),
            updatedAtEpochMs = now - twentyFourHoursMs,
            isBundledFallback = false,
        )
        val syncedFresh = FxRate(
            quoteCurrency = "USD",
            rateDecimal = BigDecimal("1.08"),
            updatedAtEpochMs = now - 3_600_000L,
            isBundledFallback = false,
        )

        assertTrue(repo.isRateStale(bundledFresh, now))
        assertTrue(repo.isRateStale(syncedOlderThan24h, now))
        assertFalse(repo.isRateStale(syncedAt24h, now))
        assertFalse(repo.isRateStale(syncedFresh, now))
    }
}
