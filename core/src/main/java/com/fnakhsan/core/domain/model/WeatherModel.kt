package com.fnakhsan.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherModel(
    val id: Int,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val iconUrl: String,
    val description: String,
    val datetime: String,
    val lastUpdate: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val visibility: Int,
    val cloudiness: Int
) : Parcelable
