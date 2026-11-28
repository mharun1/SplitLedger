package com.harun.splitledger.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApi {
    @GET("rates")
    suspend fun getRates(
        @Query("base") base: String = "EUR"
    ): List<FrankfurterRatesDto>
}