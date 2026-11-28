package com.harun.splitledger.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FrankfurterRatesDto(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)
