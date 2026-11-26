package com.harun.splitledger.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.harun.splitledger.data.local.db.entity.FxRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FxRateDao {
    @Query("SELECT * FROM fx_rate ORDER BY quoteCurrency ASC")
    fun observeRates(): Flow<List<FxRateEntity>>

    @Query("SELECT * FROM fx_rate ORDER BY quoteCurrency ASC")
    suspend fun getRates(): List<FxRateEntity>

    @Upsert
    suspend fun upsertRates(rates: List<FxRateEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRateIfMissing(rates: List<FxRateEntity>)

}