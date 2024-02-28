package com.fnakhsan.core.domain.model

data class WeatherModel(
    val id: String,
    val location: String,
    val iconUrl: String,
    val description: String,
    val datetime: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val visibility: Int,
    val cloudiness: Int,
    val windSpeed: Double
)
